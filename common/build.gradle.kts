import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    // For compose-resource
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    //applyDefaultHierarchyTemplate()
    jvm()
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
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
                implementation(compose.runtime)
                // For sharing vnWordList
                api(compose.components.resources)
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

compose.resources {
    //packageOfResClass = "com.finggallink.mmwave.notification.ui"
    //generateResClass = always
    publicResClass = true
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
}
