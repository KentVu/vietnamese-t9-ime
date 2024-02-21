@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvm()
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation(project(":common:ui"))
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
        named("jvmTest") {
            dependencies {
                //implementation(project(":sharedtest"))
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                implementation(libs.kotlinx.coroutines.test)
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
            packageName = "T9VietnameseComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}