@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
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
                api(compose.material3)
                implementation(project(":lib"))
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
                //implementation(libs.androidx.appcompat)
                //implementation(libs.androidx.core.ktx)
                //implementation(libs.androidx.compose.ui.tooling.preview)
            }
        }
        named("androidTest") {
            kotlin.srcDirs("src/jvmTest/kotlin")
            dependencies {
                implementation(libs.androidx.compose.ui.test.junit4)
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
