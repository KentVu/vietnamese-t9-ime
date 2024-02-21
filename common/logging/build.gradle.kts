plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
      }
    }
  }
  jvm()

  /*iosX64()
  iosArm64()
  iosSimulatorArm64()*/

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.napier)
      }
    }
    val androidMain by getting {
      dependencies {
      }
    }
    /*val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
    }*/
    val jvmMain by getting {
      dependencies {
      }
    }
  }
}

android {
  namespace = "com.github.kentvu.t9vietnamese.logging"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
