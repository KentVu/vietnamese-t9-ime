@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":common"))
                implementation(project(":common:ui"))
                implementation(project(":lib"))
                implementation(project(":lib:logging"))
            }
        }
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(project(":common:ui"))
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                implementation(libs.kotlinx.coroutines.test)
                // Test rules and transitive dependencies:
                implementation(compose.desktop.uiTestJUnit4)
                //implementation(project(":sharedtest"))
            }
        }
        named("commonTest") {
            dependencies {
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.kentvu.t9vietnamese.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "T9VietnameseComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}
