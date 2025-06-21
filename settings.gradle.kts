rootProject.name = "T9Vietnamese"

include(":dawg-kotlin")
//include(":lib")
include(":lib:logging")
include(":common")
include(":common:ui")
include(":app")
include(":console")
include(":desktop")
include(":webDemo")
//include(":sharedJvmTest")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
  resolutionStrategy {
      eachPlugin {
          when (requested.id.id) {
              "deploygate" ->
                  useModule("com.deploygate:gradle:2.9.0") //${required.version}
                  //useModule("${libs.}:${required.version}")
          }
      }
  }
}

dependencyResolutionManagement {
  repositories {
    //mavenLocal()
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
  /*versionCatalogs {
    create("libs") {
      version("androidxTestExt", "1.1.5")
      version("androidxTestEspresso", "3.5.1")

      library("androidx-test-ext-junit", "androidx.test.ext", "junit").versionRef("androidxTestExt")
      library(
        "androidx-test-espresso-core",
        "androidx.test.espresso",
        "espresso-core"
      ).versionRef("androidxTestEspresso")
    }
  }*/
}
