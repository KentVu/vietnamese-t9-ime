@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    android()
    //jvm("desktop")
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(project(mapOf("path" to ":lib")))
                // Needed only for preview.
//                implementation(compose.preview)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                // Test rules and transitive dependencies:
                implementation(compose.uiTestJUnit4)
            }
        }
        named("androidMain") {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.8.0")
                implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
            }
        }
        named("androidTest") {
            kotlin.srcDirs("src/jvmTest/kotlin")
            dependencies {
                implementation("androidx.compose.ui:ui-test-junit4:1.2.1")
            }
        }
        //named("desktopMain") {
        //    dependencies {
        //        implementation(compose.preview)
        //    }
        //}
    }
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}
