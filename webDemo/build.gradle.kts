import java.io.BufferedReader
import java.io.ByteArrayOutputStream

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// https://stackoverflow.com/a/72099910/1562087
/*val gitVersion = providers.exec {
  commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get()*/

kotlin {
  js(IR) {
    browser {
      testTask {
        useKarma {
          useSourceMapSupport()
          //useChrome()
          useChromeHeadless()
        }
      }
    }
    binaries.executable()
  }
  sourceSets {
    commonTest.dependencies {
      implementation(kotlin("test")) // This makes test annotations and functionality available in JS
    }
    val jsMain by getting {
      dependencies {
        implementation(project(":common"))
        implementation(project(":lib:logging"))
        implementation(compose.html.core)
        implementation(compose.runtime)
        implementation(compose.components.resources)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.okio.fakefilesystem)
      }
    }
    // https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/HTML/Using_Test_Utils/README.md#using-test-utils-for-unit-testing
    val jsTest by getting {
        dependencies {
          implementation(kotlin("test-js"))
          implementation(compose.html.testUtils)
          // For resources to be available in test also.
          implementation(compose.components.resources)
        }
    }
  }
}


