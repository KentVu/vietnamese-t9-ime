plugins {
    application
//    kotlin("multiplatform")
    kotlin("jvm")
}
/*
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
*/
// group = "cli"
// version = "0.2.0"

kotlin {
//    jvm()

    sourceSets {
        // named("jvmMain") {
        //     dependencies {
        //     }
        // }
    }
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
/*
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
//https://github.com/gradle/gradle/issues/21364
tasks.withType(JavaExec).configureEach {
    if (name.endsWith("main()")) {
        notCompatibleWithConfigurationCache("JavaExec created by IntelliJ")
    }
}
*/
