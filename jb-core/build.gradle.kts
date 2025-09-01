plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.js.optimizer)
  `maven-publish`
  signing
  alias(libs.plugins.mavenCentral.publish)
}

android {
  namespace = "io.github.porum.jb.core"
  compileSdk = 35

  defaultConfig {
    minSdk = 21

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
      withJavadocJar()
    }
//    multipleVariants("allVariants") {
//      allVariants()
//      withSourcesJar()
//      withJavadocJar()
//    }
  }
}

dependencies {
  api(project(":jb-api"))
  implementation(libs.androidx.webkit)
}

publishing {
  publications {
    register<MavenPublication>("release") {
      afterEvaluate {
        from(components["release"])

        pom {
          name.set(project.name)
          description.set("Yet another js bridge for Android.")
        }
      }
    }
  }
}