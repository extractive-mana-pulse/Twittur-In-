import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // add
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id ("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
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
        debug {
            buildConfigField ("String", "BASE_URL", "\"https://twitturin-dev.onrender.com/api/\"")
//           buildConfigField("String", "BASE_URL", "\"https://twitturin-api.onrender.com/api/\"")
        }
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://twitturin-dev.onrender.com/api/\"")
//            buildConfigField("String", "BASE_URL", "\"https://twitturin-api.onrender.com/api/\"")
        }
    }
    buildFeatures{
        viewBinding = true
//      noinspection DataBindingWithoutKapt
        dataBinding = true
//      compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.2"
//    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

//    implementation ("androidx.activity:activity-compose:$version")
//    implementation ("androidx.compose.runtime:runtime-livedata:$version")
//    implementation ("androidx.compose.ui:ui:$version")
//    implementation ("androidx.compose.ui:ui-tooling-preview:$version")
//    implementation ("androidx.compose.material:material:$version")
//    implementation ("androidx.compose.ui:ui-viewbinding:$version")
//    implementation ("androidx.navigation:navigation-compose:$version")
//
//    // rich edit text library
//    implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-beta03")
//
//    // icons jetpack compose
//    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.material3:material3:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //  Dagger hilt
    kapt ("androidx.hilt:hilt-compiler:1.1.0")
    kapt ("com.google.dagger:hilt-android-compiler:2.50")
    implementation ("com.google.dagger:hilt-android:2.50")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // Coroutines
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // liveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Navigation
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")

    // Data Binding
    implementation ("androidx.databinding:databinding-runtime:8.3.1")

    // Scroll to Refresh Layout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // LottieFiles
    implementation ("com.airbnb.android:lottie:3.5.0")

    // Country Picker
    implementation ("com.hbb20:ccp:2.5.3")

    // mask edit text
    implementation ("com.github.santalu:mask-edittext:1.0.2")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // rounding image corners
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // preference
    implementation ("androidx.preference:preference-ktx:1.2.1")

    // FCM
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    // Rich Text Editor
    implementation ("jp.wasabeef:richeditor-android:2.0.0")

}