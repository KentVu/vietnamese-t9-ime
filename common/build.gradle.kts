plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    //applyDefaultHierarchyTemplate()
    jvm()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
    }
    /*js(IR) {
        browser()
    }*/

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":dawg-kotlin"))
                implementation(project(":lib:logging"))
//                implementation(kotlin("coroutines"))
                implementation(libs.kotlinx.coroutines.core)
                //implementation("org.jetbrains:annotations:15.0")
                api(libs.okio)
                implementation(libs.doistx.normalize)
            }
        }
        named("androidMain") {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
            }
        }
        val jvmMain by getting {
            dependencies {
                //implementation("com.squareup.okio:okio:3.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation(libs.okio.fakefilesystem)
                implementation("com.willowtreeapps.assertk:assertk:0.25")
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.github.kentvu.t9vietnamese"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
            // Share resources from commonMain
            // https://luisramos.dev/how-to-share-resources-kmm
            resources {
                srcDir("src/commonMain/resources")
            }
        }
    }
}
