plugins {
    application
    //kotlin("multiplatform")
    kotlin("jvm")
}

// group = "cli"
// version = "0.2.0"

kotlin {
    //jvm()

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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
