import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Search feature — presentation layer: MVI (State/Action/Event), SearchViewModel,
// SearchRoot/SearchScreen composables, Koin module.
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.twitturin.feature.search.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.feature.search.domain)
            implementation(projects.core.presentation)
            implementation(projects.core.designSystem)
            implementation(libs.bundles.compose)
            implementation(libs.bundles.lifecycleCompose)
            implementation(libs.bundles.koinCompose)
            implementation(libs.bundles.coil)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            // Speech-recognizer launcher behind the search field's mic.
            implementation(libs.androidx.activity.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
