plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gamematch"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.gamematch"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    // Material Design components (ChipGroup, MaterialButton, BottomNavigationView)
    implementation("com.google.android.material:material:1.12.0")

    // ViewModel & LiveData (keeps screen data alive on rotation and connects UI to data)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")

    // Jetpack Navigation (handles switching between our 4 screens cleanly)
    implementation("androidx.navigation:navigation-fragment:2.8.4")
    implementation("androidx.navigation:navigation-ui:2.8.4")
    // Glide (loads game cover images from URLs and caches them offline)
    implementation("com.github.bumptech.glide:glide:4.16.0")
}