plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
}

android {
  namespace = "io.github.porum.jb.example.lib"
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
}

ksp {
  arg("MODULE_NAME", project.name)
}

dependencies {
  ksp(project(":jb-annotation-processor"))
  implementation(project(":jb-api"))
  implementation(project(":jb-core"))
}