plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
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

    jvm()
    /*js(IR) {
        browser()
    }*/

    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                implementation(project(":common"))
                implementation(project(":lib:logging"))
                // Needed only for preview.
//                implementation(compose.preview)
            }
        }
        named("commonTest") {
            dependencies {
                implementation(libs.junit)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                //implementation(libs.mockative)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                // Test rules and transitive dependencies:
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
        named("androidMain") {
            dependencies {
                api(libs.androidx.activity.activityCompose)
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                implementation(libs.androidx.compose.ui.tooling.preview)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.compose.ui.uiTestJunit4)
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
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.github.kentvu.t9vietnamese.ui"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
