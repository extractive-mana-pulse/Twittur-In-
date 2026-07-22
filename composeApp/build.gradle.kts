import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // iOS framework consumed by the Xcode app (see iosApp/). Apple Silicon device + simulator.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.example.twitturin.composeApp")
        }
    }

    // Desktop (consumed by :desktopApp) and Web (Compose/Wasm) targets.
    jvm()

    androidLibrary {
        namespace = "com.example.twitturin.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            // Core + feature modules (added as each feature is ported into Track A)
            implementation(projects.core.data)
            implementation(projects.core.presentation)
            implementation(projects.core.designSystem)
            implementation(projects.feature.search.data)
            implementation(projects.feature.search.presentation)
            implementation(projects.feature.notification.data)
            implementation(projects.feature.notification.presentation)
            implementation(projects.feature.auth.data)
            implementation(projects.feature.auth.presentation)
            implementation(projects.feature.home.presentation)
            implementation(projects.feature.profile.data)
            implementation(projects.feature.profile.presentation)
            implementation(projects.feature.tweet.data)
            implementation(projects.feature.tweet.presentation)
            implementation(projects.feature.follow.data)
            implementation(projects.feature.follow.presentation)
            implementation(projects.feature.settings.presentation)
            implementation(projects.feature.timetable.data)
            implementation(projects.feature.timetable.presentation)
            implementation(projects.core.domain)

            implementation(libs.bundles.compose)
            implementation(libs.bundles.lifecycleCompose)
            implementation(libs.koin.core)
            implementation(libs.bundles.koinCompose)
            implementation(libs.bundles.coil)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
