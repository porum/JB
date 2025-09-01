package io.github.porum.convention

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

/**
 * Created by panda on 2025/8/26 13:26
 */
class JsOptimizePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val androidComponents = project.extensions.getByType(
      AndroidComponentsExtension::class.java
    )

    androidComponents.onVariants { variant ->
      val optimizeAssetsJsTaskProvider = project.tasks.register(
        "optimize${variant.name.capitalized()}AssetsJs",
        JsOptimizeTask::class.java
      )
      variant.artifacts.use(optimizeAssetsJsTaskProvider)
        .wiredWithDirectories(
          JsOptimizeTask::inputDir,
          JsOptimizeTask::outputDir
        ).toTransform(SingleArtifact.ASSETS)
    }
  }
}