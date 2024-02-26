rootProject.name = "T9Vietnamese"

include(":dawg-kotlin")
include(":lib")
include(":lib:logging")
include(":common")
include(":common:ui")
include(":app")
//include(":console")
include(":desktop")
//include(":web")
//include(":sharedtest")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        //mavenLocal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
