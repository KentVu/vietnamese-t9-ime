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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains:annotations:15.0")
                implementation("com.squareup.okio:okio:3.2.0")
                implementation("com.doist.x:normalize:1.0.3")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation("com.squareup.okio:okio-fakefilesystem:3.2.0")
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.google.truth:truth:1.1.3")
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
