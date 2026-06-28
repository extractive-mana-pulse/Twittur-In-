package com.example.twitturin.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TwitturBlue,
    onPrimary = TwitturOnPrimary,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = TwitturBlue,
    onPrimary = TwitturOnPrimary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
)

/** App-wide Compose theme. Wrap every platform entry's content in this. */
@Composable
fun TwitturTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
