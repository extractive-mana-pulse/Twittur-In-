import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// CollapsibleChatThread — github.com/extractive-mana-pulse/chat_thread_lib vendored verbatim
// (see README.md in this module). The upstream JitPack artifact is an Android-only AAR, so the
// pure-Compose source is compiled here as commonMain to serve Android + iOS + Desktop alike.
kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()

    androidLibrary {
        namespace = "com.example.chatthread"
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
