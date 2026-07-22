package com.example.twitturin.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Brand,
    onPrimary = OnBrand,
    primaryContainer = BrandSoft,
    onPrimaryContainer = BrandDeep,
    secondary = BrandDeep,
    onSecondary = OnBrand,
    secondaryContainer = BrandSoft,
    onSecondaryContainer = BrandDeep,
    background = Background,
    onBackground = Ink,
    surface = Background,
    onSurface = Ink,
    surfaceVariant = NavSurface,
    onSurfaceVariant = SecondaryText,
    surfaceContainer = SurfaceMuted,
    surfaceContainerHigh = SurfaceMuted,
    outline = Hint,
    outlineVariant = DividerLine,
    error = Danger,
    onError = OnBrand,
    scrim = Ink,
)

private val DarkColors = darkColorScheme(
    primary = Brand,
    onPrimary = OnBrand,
    primaryContainer = BrandSoftDark,
    onPrimaryContainer = BrandSoft,
    secondary = Brand,
    onSecondary = OnBrand,
    secondaryContainer = BrandSoftDark,
    onSecondaryContainer = BrandSoft,
    background = BackgroundDark,
    onBackground = InkDark,
    surface = BackgroundDark,
    onSurface = InkDark,
    surfaceVariant = NavSurfaceDark,
    onSurfaceVariant = SecondaryTextDark,
    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceDark,
    outline = Hint,
    outlineVariant = DividerLineDark,
    error = Danger,
    onError = OnBrand,
)

private val TwitturShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun TwitturTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = Brand,
    content: @Composable () -> Unit,
) {
    val base = if (darkTheme) DarkColors else LightColors
    val scheme = base.copy(
        primary = accent,
        secondary = accent,
        primaryContainer = accent.copy(alpha = 0.14f),
        onPrimaryContainer = accent,
    )
    MaterialTheme(
        colorScheme = scheme,
        typography = twitturTypography(),
        shapes = TwitturShapes,
        content = content,
    )
}
