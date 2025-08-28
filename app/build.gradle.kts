import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    }
  }
  sourceSets {
    val androidMain by getting {
      dependencies {
        implementation(project(":common"))
        implementation(project(":common:ui"))
        implementation(project(":lib:logging"))
        //implementation(libs.androidx.compose.ui.tooling.preview)
      }
    }
    val androidUnitTest by getting {
      dependencies {
        // Local tests: jUnit, coroutines, Android runner
        implementation(libs.junit)
      }
    }
    val androidInstrumentedTest by getting {
      // Instrumented tests: jUnit rules and runners
      dependencies {
        //implementation(project(":sharedJvmTest"))
        implementation(libs.androidx.compose.ui.uiTestJunit4)
        //implementation(libs.androidx.test.ext.junit)
        //androidTestImplementation(libs.androidx.test.espresso.core)
      }
    }
  }
}

android {
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  namespace = "com.github.kentvu.t9vietnamese"

  defaultConfig {
    applicationId = "com.github.kentvu.t9vietnamese"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures { compose = true }

  signingConfigs {
    // https://github.com/Julow/Unexpected-Keyboard/blob/master/CONTRIBUTING.md#specifying-a-debug-signing-certificate-on-github-actions
    // Debug builds will always be signed. If no environment variables are set, a default
    // keystore will be initialized by the task initDebugKeystore and used. This keystore
    // can be uploaded to GitHub secrets by following instructions in CONTRIBUTING.md
    // in order to always receive correctly signed debug APKs from the CI.
    getByName("debug") {
      val keystorePath = System.getenv("DEBUG_KEYSTORE")
      if (keystorePath != null) {
        storeFile = file(keystorePath)
        storePassword = System.getenv("DEBUG_KEYSTORE_PASSWORD")// ?: "debug0"
        keyAlias = System.getenv("DEBUG_KEY_ALIAS")// ?: "debug"
        keyPassword = System.getenv("DEBUG_KEY_PASSWORD")// ?: "debug0"
      }
    }

  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
    implementation(libs.androidx.compose.ui.tooling.preview)
}
