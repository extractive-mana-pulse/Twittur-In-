rootProject.name = "Twittur"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ---------------------------------------------------------------------------
// New Compose Multiplatform build.
// NOTE: the legacy single-module Android app (":app") is intentionally NOT
// included here. Its sources stay on disk as a dormant reference (still
// buildable from the `main` branch) and are removed at cutover. See the
// migration plan: .claude/plans/i-want-to-update-elegant-flute.md
// ---------------------------------------------------------------------------

// Shared Compose UI aggregator (root App() + NavHost + DI). Exports the iOS framework.
include(":composeApp")

// Thin per-platform entry points
include(":androidApp")
include(":desktopApp")
// include(":webApp")   // Compose/Wasm — added after the Android/iOS/Desktop targets are green

// Core + feature modules (added incrementally as each is built)
include(":core:domain")
// include(":core:data"); include(":core:presentation"); include(":core:design-system")
// include(":feature:search:domain"); ... etc.
