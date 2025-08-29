package io.github.porum.jb.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import io.github.porum.jb.api.Name

class MetadataCollector(
  private val metadataList: MutableList<Metadata>,
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

    metadataList.add(Metadata(bridgeName, className, source))
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