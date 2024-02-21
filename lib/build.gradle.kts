plugins {
    id("com.android.library")
    kotlin("multiplatform")
}
/*
java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
   implementation project(path: ':DAWG')
   implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"

   testImplementation "org.jetbrains.kotlin:kotlin-test"
   //testImplementation "org.jetbrains.kotlin:kotlin-test-junit5"
   //testImplementation "junit:junit:$junit_version"
   testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version"
   testImplementation 'app.cash.turbine:turbine:0.8.0'
   //testImplementation "org.mockito.kotlin:mockito-kotlin:$mockito_version"
}

compileTestKotlin {
   kotlinOptions {
       jvmTarget = "1.8"
       //freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
   }
}

test {
   useJUnitPlatform()
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
   kotlinOptions {
       freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
       freeCompilerArgs += "-Xopt-in=kotlin.E"
   }
}
*/

kotlin {
    android()
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":dawg-kotlin"))
//                implementation(kotlin("coroutines"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains:annotations:15.0")
                api("com.squareup.okio:okio:3.2.0")
                implementation("com.doist.x:normalize:1.0.3")
                api("io.github.aakira:napier:2.6.1")//$napierVersion
            }
        }
        named("androidMain") {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation("com.squareup.okio:okio-fakefilesystem:3.2.0")
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
            // Share resources from commonMain
            // https://luisramos.dev/how-to-share-resources-kmm
            resources {
                srcDir("src/commonMain/resources")
            }
        }
    }
}

/*
dependencies {
    implementation(project(":DAWG"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("app.cash.turbine:turbine:0.8.0")
}
*/
