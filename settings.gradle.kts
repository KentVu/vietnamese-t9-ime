pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    versionCatalogs {
        create("libs") {
            version("androidGradlePlugin", "7.4.2")
            version("androidxCore", "1.9.0")
            version("androidxLifecycle", "2.6.0")
            version("androidxActivity", "1.6.1")
            version("androidxComposeBom", "2023.03.00")
            version("androidxComposeCompiler", "1.4.3")
            version("androidxTestExt", "1.1.5")
            version("junit", "4.13.2")
            version("kotlin", "1.8.10")
            version("androidxTestEspresso", "3.5.1")
            version("androidxNavigation", "2.5.3")
            version("compose", "1.3.1")
            version("coroutines", "1.6.4")

            library("androidx-core-ktx", "androidx.core", "core-ktx").versionRef("androidxCore")
            library("androidx-lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx").versionRef("androidxLifecycle")
            library("androidx-navigation-compose", "androidx.navigation", "navigation-compose").versionRef("androidxNavigation")
            library("androidx-activity-compose", "androidx.activity", "activity-compose").versionRef("androidxActivity")
            library("androidx-appcompat", "androidx.appcompat:appcompat:1.5.1")
            library("androidx-compose-bom", "androidx.compose", "compose-bom").versionRef("androidxComposeBom")
            library("androidx-compose-ui", "androidx.compose.ui", "ui").withoutVersion()
            library("androidx-compose-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview").withoutVersion()
            library("androidx-compose-material3", "androidx.compose.material3", "material3").withoutVersion()
            library("junit", "junit", "junit").versionRef("junit")
            library("androidx-test-ext-junit", "androidx.test.ext", "junit").versionRef("androidxTestExt")
            library("androidx-test-espresso-core", "androidx.test.espresso", "espresso-core").versionRef("androidxTestEspresso")
            library("androidx-compose-ui-test-junit4", "androidx.compose.ui", "ui-test-junit4").withoutVersion()
            library("androidx-compose-ui-tooling", "androidx.compose.ui", "ui-tooling").withoutVersion()
            library("androidx-compose-ui-test-manifest", "androidx.compose.ui", "ui-test-manifest").withoutVersion()
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx",
                "kotlinx-coroutines-android").versionRef("coroutines")
            library("squareup-okio", "com.squareup.okio:okio:3.2.0")

            plugin("android-application", "com.android.application").versionRef("androidGradlePlugin")
            plugin("android-library", "com.android.library").versionRef("androidGradlePlugin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").versionRef("kotlin")
            plugin("jetbrains-compose", "org.jetbrains.compose").versionRef("compose")
        }
    }
}

rootProject.name = "T9Vietnamese"
include(":app")
include(":lib")
include(":dawg-kotlin")
include(":common")
//include(":dawg-kotlin", ":lib", ":common")
//include(":desktop", :console", ":web")
include(":sharedtest")
