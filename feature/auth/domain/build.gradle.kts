import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

// Auth feature — domain layer: registration models + AuthRepository contract.
// Pure Kotlin/KMP, depends only on :core:domain (Result/DataError/SessionSource).
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.twitturin.feature.auth.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
