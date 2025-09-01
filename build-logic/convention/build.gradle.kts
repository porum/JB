import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  alias(libs.plugins.android.lint)
}

group = "io.github.porum.buildlogic"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
  }
}

dependencies {
  gradleApi()
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.android.gradlePluginApi)
  compileOnly(libs.android.tools.common)
  implementation(libs.closure.compiler)
}

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

gradlePlugin {
  plugins {
    register("js-optimizer") {
      id = libs.plugins.js.optimizer.get().pluginId
      implementationClass = "io.github.porum.convention.JsOptimizePlugin"
    }
    register("mavenCentral-publish") {
      id = libs.plugins.mavenCentral.publish.get().pluginId
      implementationClass = "io.github.porum.convention.MavenCentralPublishPlugin"
    }
  }
}