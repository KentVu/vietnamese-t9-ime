@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation(compose.uiTestJUnit4)
    api(project(":common:ui"))
    implementation(project(":lib"))
    implementation(project(":lib:logging"))
    implementation(kotlin("test")) // This brings all the platform dependencies automatically
    implementation(libs.kotlinx.coroutines.test)
    //implementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.20")
}
