import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

// Notification feature — domain layer: ReleaseInfo model + NotificationRepository contract.
// Pure Kotlin/KMP, depends only on :core:domain (Result/DataError).
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.twitturin.feature.notification.domain"
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
