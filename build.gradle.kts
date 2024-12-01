buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        // in build.gradle
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id ("androidx.navigation.safeargs") version "2.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}