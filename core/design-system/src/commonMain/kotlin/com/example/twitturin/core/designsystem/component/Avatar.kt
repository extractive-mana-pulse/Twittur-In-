package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twitturin.core.designsystem.theme.AvatarGradients
import com.example.twitturin.core.designsystem.theme.OnBrand

/** Picks a stable avatar gradient for a name/seed. */
fun avatarGradientFor(seed: String): Brush {
    val idx = if (seed.isEmpty()) 0 else (seed.sumOf { it.code } % AvatarGradients.size)
    return Brush.linearGradient(AvatarGradients[idx])
}

/**
 * Circular initial-on-gradient avatar (the redesign's default when there's no photo).
 * For photo avatars, layer a Coil `AsyncImage` clipped to a circle in the feature module and
 * fall back to this when the URL is null.
 */
@Composable
fun GradientAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    fontSize: TextUnit = (size.value * 0.38f).sp,
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarGradientFor(name)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = OnBrand,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
        )
    }
}
