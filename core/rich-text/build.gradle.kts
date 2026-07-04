import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// RichTextEditor — github.com/extractive-mana-pulse/RichTextEditor vendored (see README.md in
// this module). Upstream is an Android-only demo app, so its rich_text_editor package is
// compiled here as commonMain to serve Android + iOS + Desktop alike, with the Android
// resource references replaced (drawables → ImageVectors, res/font → composeResources).
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.richtexteditor"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Montserrat + PT Serif (the editor's two selectable fonts) live in commonMain/composeResources.
// Pin the generated accessor package so callers import a stable path.
compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "com.example.richtexteditor.resources"
}
