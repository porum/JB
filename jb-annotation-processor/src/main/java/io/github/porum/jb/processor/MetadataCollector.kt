package io.github.porum.jb.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name

class MetadataCollector(
  private val metadataList: MutableList<Metadata>,
  private val resolver: Resolver,
  private val logger: KSPLoggerWrapper
) : KSVisitorVoid() {

  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
    super.visitClassDeclaration(classDeclaration, data)

    val className = classDeclaration.qualifiedName?.asString()
    if (className == null) {
      logger.error(ERROR_ILLEGAL_CLASS_NAME)
      return
    }

    val source = classDeclaration.containingFile
    if (source == null) {
      logger.error(ERROR_ILLEGAL_SOURCE_FILE)
      return
    }

    val annotation = classDeclaration.annotations.first {
      it.shortName.asString() == Name::class.simpleName
    }
    val bridgeName = annotation.getParamValueByKey("value")?.toString()
    if (bridgeName.isNullOrEmpty()) {
      logger.error(ERROR_ILLEGAL_BRIDGE_NAME)
      return
    }

    val jbClass = resolver.getClassDeclarationByName(
      resolver.getKSNameFromString(JB::class.qualifiedName!!)
    )
    classDeclaration.superTypes.forEach { superTypeRef ->
      val superType = superTypeRef.resolve()
      if (superType.declaration == jbClass) {
        val genericArguments = superType.arguments
        if (genericArguments.isNotEmpty()) {
          val type = genericArguments[0].type?.resolve()
          val name = type?.declaration?.qualifiedName?.asString()
        }
      }
    }

    val superType = classDeclaration.superTypes.map { it.resolve() }.firstOrNull { it.declaration == jbClass }
    if (superType == null) {
      return
    }
    val genericArguments = superType.arguments
    if (genericArguments.isEmpty()) {
      return
    }

    val genericType = genericArguments[0].type?.resolve()
    val genericClassName = genericType?.declaration?.qualifiedName?.asString() ?: return

    metadataList.add(Metadata(bridgeName, className, genericClassName, source))
  }

  private fun KSAnnotation.getParamValueByKey(key: String) = arguments
    .first { it.name?.asString() == key }
    .value

  companion object {
    const val ERROR_ILLEGAL_CLASS_NAME = "The annotated class is not valid with qualified name."
    const val ERROR_ILLEGAL_SOURCE_FILE = "The annotated class is not come from a source file."
    const val ERROR_ILLEGAL_BRIDGE_NAME = "The annotated class is not valid with bridge name."
  }
}