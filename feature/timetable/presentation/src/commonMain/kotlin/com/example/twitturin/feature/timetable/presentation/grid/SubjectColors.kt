package com.example.twitturin.feature.timetable.presentation.grid

import androidx.compose.ui.graphics.Color

/**
 * A fixed palette spanning the hue wheel at the same moderate saturation/lightness as the rest
 * of the brand palette (see `core/design-system` `Color.kt`'s `Avatar*` constants), so a dense
 * grid of dozens of subjects still reads as one cohesive app rather than default Material hues.
 * Picked deterministically per subject id — same idea as `avatarGradientFor`, but a flat colour
 * (lesson blocks want a solid fill, not a gradient) and a wider palette (a real timetable easily
 * has 10+ distinct followed subjects, more than the 4-gradient avatar palette needs to cover).
 */
private val SubjectPalette = listOf(
    Color(0xFF1574A6), // brand blue
    Color(0xFF43A047), // green
    Color(0xFF8E24AA), // purple
    Color(0xFFFB8C00), // orange
    Color(0xFFE0245E), // danger/pink
    Color(0xFF00897B), // teal
    Color(0xFF5E35B1), // indigo
    Color(0xFFC0862B), // amber-brown
    Color(0xFF3949AB), // deep blue
    Color(0xFF6D4C41), // brown
)

fun subjectColorFor(subjectId: String): Color {
    if (subjectId.isEmpty()) return SubjectPalette[0]
    val index = subjectId.sumOf { it.code } % SubjectPalette.size
    return SubjectPalette[index]
}
