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
    implementation(project(":common"))
    implementation(project(":lib"))
    implementation(kotlin("test")) // This brings all the platform dependencies automatically
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.20")
}
