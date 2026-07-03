package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** clickable with no ripple/indication — handy for small inline icon toggles. */
internal fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick,
    ),
)

/** Draws a bottom border line (used by underlined inputs). */
internal fun Modifier.drawBottomBorder(color: Color, width: Dp = 2.dp): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = width.toPx()
        val y = size.height - stroke / 2f
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = stroke,
        )
    },
)
