package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.BrandSoft
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.Professor
import com.example.twitturin.core.designsystem.theme.SurfaceMuted

/** Role chip: "student" (brand) / "professor"/"teacher" (green). */
@Composable
fun KindBadge(kind: String, modifier: Modifier = Modifier) {
    val isProfessor = kind.equals("professor", true) || kind.equals("teacher", true)
    val fg = if (isProfessor) Professor else Brand
    val bg = if (isProfessor) Professor.copy(alpha = 0.12f) else BrandSoft
    Pill(text = kind, fg = fg, bg = bg, modifier = modifier)
}

/** Small uppercase pill used for language ("EN"). */
@Composable
fun LanguagePill(text: String, modifier: Modifier = Modifier) {
    Pill(text = text, fg = Ink, bg = SurfaceMuted, modifier = modifier, weight = FontWeight.Bold)
}

@Composable
private fun Pill(
    text: String,
    fg: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.SemiBold,
) {
    Text(
        text = text,
        modifier = modifier
            .background(bg, RoundedCornerShape(11.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = fg,
        fontWeight = weight,
        style = MaterialTheme.typography.labelMedium,
    )
}
