package io.github.porum.convention

import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.CompilerOptions.LanguageMode
import com.google.javascript.jscomp.PropertyRenamingPolicy
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.jscomp.VariableRenamingPolicy
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class JsOptimizeTask : DefaultTask() {

  @get:InputDirectory
  abstract val inputDir: DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @TaskAction
  fun taskAction() {
    inputDir.get().asFile.copyRecursively(outputDir.get().asFile, overwrite = true)

    val compiler = Compiler(System.out)
    val options = CompilerOptions().apply {
      setLanguageIn(LanguageMode.ECMASCRIPT_2020)
      setLanguageOut(LanguageMode.ECMASCRIPT_2015)
      setVariableRenaming(VariableRenamingPolicy.ALL)
      propertyRenaming = PropertyRenamingPolicy.OFF
      environment = CompilerOptions.Environment.BROWSER
      isPrettyPrint = false
    }
//    val externs = SourceFile.fromZipFile(
//      "${project.rootDir}/build-logic/convention/src/main/resources/externs.zip",
//      StandardCharsets.UTF_8
//    ).plus(
//      SourceFile.fromFile(
//        "${project.rootDir}/build-logic/convention/src/main/resources/bridge.js",
//        StandardCharsets.UTF_8
//      )
//    )

    outputDir.get().asFileTree.matching {
      setIncludes(listOf("**/bridge.js"))
    }.files.forEach { file ->
      val result = compiler.compile(
        emptyList(),
        listOf(SourceFile.fromFile(file.absolutePath)),
        options
      )

      if (result.success) {
        file.writeText(compiler.toSource())
      }
    }
  }
}