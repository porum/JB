plugins {
  id("java-library")
  alias(libs.plugins.jetbrains.kotlin.jvm)
  alias(libs.plugins.kotlinx.serialization)
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
  compilerOptions {
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
  }
}

dependencies {
  implementation(project(":jb-api"))
  implementation(libs.ksp.api)
  implementation(libs.kotlinpoet)
  implementation(libs.kotlinpoet.ksp)
  implementation(libs.kotlinx.serialization)
}
