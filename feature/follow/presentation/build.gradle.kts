import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Follow feature — presentation layer: MVI (State/Action/Event), FollowViewModel,
// FollowersRoot/FollowingRoot/FollowListScreen composables, Koin module.
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.twitturin.feature.follow.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.feature.follow.domain)
            implementation(projects.core.presentation)
            implementation(projects.core.designSystem)
            implementation(libs.bundles.compose)
            implementation(libs.bundles.lifecycleCompose)
            implementation(libs.bundles.koinCompose)
            implementation(libs.bundles.coil)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
