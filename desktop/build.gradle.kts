@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")// version "1.7.20"
    id("org.jetbrains.compose")// version "1.2.1"
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation(project(":lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                // Test rules and transitive dependencies:
                implementation(compose.uiTestJUnit4)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}