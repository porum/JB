import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
}

android {
  namespace = "io.github.porum.jb.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "io.github.porum.jb.example"
    minSdk = 21
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }

  val props = loadProperties(rootProject.file("sign.properties").path)
  signingConfigs {
    create("release") {
      keyAlias = props["key_alias"] as String
      keyPassword = props["key_password"] as String
      storeFile = file(props["store_file"] as String)
      storePassword = props["store_password"] as String
      enableV1Signing = true
      enableV2Signing = true
      enableV3Signing = true
      enableV4Signing = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = false
      signingConfig = signingConfigs.getByName("release")
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

  viewBinding {
    enable = true
  }

  packaging {

  }
}

ksp {
  arg("MODULE_NAME", project.name)
}

dependencies {
  implementation(project(":example-lib"))

  ksp(project(":jb-annotation-processor"))
  implementation(project(":jb-api"))
  implementation(project(":jb-core"))

  implementation(libs.androidx.webkit)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity)
  implementation(libs.material)
}