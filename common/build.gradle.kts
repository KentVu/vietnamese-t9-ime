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
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":dawg-kotlin"))
                implementation(project(":lib:logging"))
                implementation(libs.kotlinx.coroutines.core)
                //just want to use the library without applying the plugin.
                implementation("org.jetbrains.compose.runtime:runtime:" + libs.versions.compose.multiplatform.get())
                //could've done this but I don't want to apply the pkugin.
                //implementation(compose.runtime)
                api(libs.okio)
                implementation(libs.doistx.normalize)
            }
        }
        named("androidMain") {
            kotlin.srcDirs("src/jvmMain/kotlin")
        }
        val jvmMain by getting {
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
    namespace = "com.github.kentvu.t9vietnamese.common"

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
