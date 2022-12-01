plugins {
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
    jvm() {
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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                implementation("app.cash.turbine:turbine:0.12.1")
            }
        }
        named("jvmTest") {
            dependencies {
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
