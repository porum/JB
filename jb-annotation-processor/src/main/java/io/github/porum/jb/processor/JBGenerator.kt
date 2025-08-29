package io.github.porum.jb.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.JBFactory

class JBGenerator(
  private val codeGenerator: CodeGenerator,
) {

  fun generate(list: List<Metadata>) {
    val JBClassName = ClassName(
      JB::class.qualifiedName!!.substringBeforeLast("."),
      JB::class.qualifiedName!!.substringAfterLast("."),
    )
    val JBFactoryClassName = ClassName(
      JBFactory::class.qualifiedName!!.substringBeforeLast("."),
      JBFactory::class.qualifiedName!!.substringAfterLast("."),
    )

    list.forEach {
      val bridgeName = it.bridgeName
      val packageName = it.className.substringBeforeLast(".")
      val simpleClassName = it.className.substringAfterLast(".")
      val genericClassName = ClassName(
        it.genericClassName.substringBeforeLast("."),
        it.genericClassName.substringAfterLast(".")
      )

      val file = FileSpec.builder(GENERATE_PACKAGE_NAME, "JBFactory_$bridgeName")
        .addType(
          TypeSpec.classBuilder("JBFactory_$bridgeName")
            .addSuperinterface(JBFactoryClassName.plusParameter(genericClassName))
            .addFunction(
              FunSpec.builder("getName")
                .addModifiers(KModifier.OVERRIDE)
                .returns(String::class)
                .addStatement("return %S", bridgeName)
                .build()
            )
            .addFunction(
              FunSpec.builder("getJB")
                .addModifiers(KModifier.OVERRIDE)
                .returns(JBClassName.plusParameter(genericClassName))
                .addStatement(
                  "return %T()",
                  ClassName(packageName, simpleClassName)
                )
                .build()
            )
            .build()
        )
        .build()

      file.writeTo(codeGenerator, false)
    }

    val serviceFile = codeGenerator.createNewFile(
      dependencies = Dependencies(true, *list.map { it.sourceFile }.toTypedArray()),
      packageName = "",
      fileName = "META-INF/services/$SERVICE_NAME",
      extensionName = ""
    )
    serviceFile.bufferedWriter().use { writer ->
      list.forEach {
        writer.write("$GENERATE_PACKAGE_NAME.JBFactory_${it.bridgeName}\n")
      }
    }
  }

  companion object {
    private const val GENERATE_PACKAGE_NAME = "io.github.porum.jb.generate"
    private const val SERVICE_NAME = "io.github.porum.jb.api.JBFactory"
  }
}