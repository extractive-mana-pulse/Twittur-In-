package com.example.twitturin.core.designsystem.component

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Lets a full-screen tab ask the app shell to hide or reveal the shared bottom navigation bar —
 * e.g. an immersive scroll in landscape, where every pixel of height counts. The shell
 * (`AppNavigation`) owns one instance, provides it through [LocalBottomBarVisibility], and drives
 * the bar's `AnimatedVisibility` from [isVisible]. A screen that hides the bar must restore it when
 * it leaves (an `onDispose`) so other tabs never inherit a hidden bar.
 *
 * The default is a detached no-op controller, so a screen rendered outside the shell (previews,
 * tests, the desktop rail which has no bottom bar) drives nothing and stays correct.
 */
@Stable
class BottomBarVisibilityController {
    var isVisible by mutableStateOf(true)
}

val LocalBottomBarVisibility = staticCompositionLocalOf { BottomBarVisibilityController() }
