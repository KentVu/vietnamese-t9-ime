plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.compose)
}

kotlin {
  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
      }
    }
  }
  sourceSets {
    val androidMain by getting {
      dependencies {
        implementation(project(":common"))
        implementation(project(":common:ui"))
        implementation(project(":lib:logging"))
        implementation(libs.androidx.compose.ui.tooling.preview)
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
        implementation(project(":sharedJvmTest"))
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
