plugins {
  id("java-library")
  alias(libs.plugins.jetbrains.kotlin.jvm)
  `maven-publish`
  signing
  alias(libs.plugins.mavenCentral.publish)
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
  compileOnly(project(":jb-android-stub"))
}

publishing {
  publications {
    create<MavenPublication>("JBArtifact") {
      from(components["java"])

      pom {
        name.set(project.name)
        description.set("Yet another js bridge for Android.")
      }
    }
  }
}