plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // add
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.twitturin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.twitturin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //  Dagger hilt
    implementation ("com.google.dagger:hilt-android:2.42")
    kapt ("com.google.dagger:hilt-android-compiler:2.28.3-alpha")
    implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt ("androidx.hilt:hilt-compiler:1.1.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")

    //    Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // liveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Data Binding
    implementation ("androidx.databinding:databinding-runtime:8.1.2")

    // Scroll to Refresh Layout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}