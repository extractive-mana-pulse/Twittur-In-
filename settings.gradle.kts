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
include(":core:data")
include(":core:presentation")
include(":core:design-system")
// :core:chat-thread — vendored CollapsibleChatThread lib (threaded replies UI); see its README.
include(":core:chat-thread")
// :core:rich-text — vendored RichTextEditor lib (formatted tweets/replies); see its README.
include(":core:rich-text")
// :feature:search — pilot vertical slice (Track A). Layers added as each is built.
include(":feature:search:domain")
include(":feature:search:data")
include(":feature:search:presentation")
// :feature:notification — 2nd feature (GitHub latest-release / patch notes).
include(":feature:notification:domain")
include(":feature:notification:data")
include(":feature:notification:presentation")
// :feature:auth — 3rd feature (sign-in / kind / registration / stayIn; writes the session).
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:auth:presentation")
// :feature:home — post-login hub (presentation-only for now).
include(":feature:home:presentation")
// :feature:profile — profile view / edit / delete (image picker deferred).
include(":feature:profile:domain")
include(":feature:profile:data")
include(":feature:profile:presentation")
// :feature:tweet — feed / compose / delete (like/edit/replies/report deferred).
include(":feature:tweet:domain")
include(":feature:tweet:data")
include(":feature:tweet:presentation")
// :feature:follow — followers / following lists + follow/unfollow actions.
include(":feature:follow:domain")
include(":feature:follow:data")
include(":feature:follow:presentation")
// :feature:settings — settings hub / feedback / theme + language pickers (presentation-only).
include(":feature:settings:presentation")
