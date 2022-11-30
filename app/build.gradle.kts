plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.github.kentvu.t9vietnamese.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
//        vectorDrawables {
//            useSupportLibrary true
//        }
    }

//    buildTypes {
//        release {
//            minifyEnabled = false
//            proguardFiles = getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
//    kotlinOptions {
//        jvmTarget = '1.8'
//    }
//    buildFeatures {
//        compose true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion compose_version
//    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {

    implementation(project(":lib"))
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.5.0")

//    implementation "androidx.core:core-ktx:1.7.0"
//    implementation "androidx.compose.ui:ui:$compose_version"
//    implementation "androidx.compose.material:material:$compose_version"
//    implementation "androidx.compose.ui:ui-tooling-preview:$compose_version"
//    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.1'
//    implementation 'androidx.appcompat:appcompat:1.3.0'
//    implementation 'com.google.android.material:material:1.4.0'
//    testImplementation "junit:junit:$junit_version"
//    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
//    androidTestImplementation 'androidx.test:rules:1.4.0'
//    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
//    androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
//    debugImplementation "androidx.compose.ui:ui-tooling:$compose_version"
    // Needed for createComposeRule, but not createAndroidComposeRule:
//    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
}