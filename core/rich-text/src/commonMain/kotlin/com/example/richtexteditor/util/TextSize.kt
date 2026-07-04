package com.example.richtexteditor.util

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Selectable font sizes (upstream's Small/Medium/Large kept verbatim). [Default] (a Twittur
 * addition) inherits the size of the style the text is rendered with, so unstyled tweet text
 * matches the rest of the card.
 */
enum class TextSize(
    val size: TextUnit?,
    /** Stable token used by the wire format — never rename. */
    val token: String,
) {
    Small(12.sp, "sm"),
    Default(null, "default"),
    Medium(18.sp, "md"),
    Large(34.sp, "lg");

    fun label(): String = when (this) {
        Small -> "Small"
        Default -> "Default"
        Medium -> "Medium"
        Large -> "Large"
    }

    companion object {
        fun fromToken(token: String): TextSize? = entries.firstOrNull { it.token == token }
    }
}
