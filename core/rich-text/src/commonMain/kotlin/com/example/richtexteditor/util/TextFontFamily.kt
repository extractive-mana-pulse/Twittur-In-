package com.example.richtexteditor.util

/**
 * Selectable font families. Upstream bound each entry to an Android `R.font` id; in commonMain
 * the actual [androidx.compose.ui.text.font.FontFamily] is resolved from compose resources via
 * `rememberRichTextFonts()`. [Default] inherits the app's own font (DM Sans in Twittur).
 */
enum class TextFontFamily(
    /** Stable token used by the wire format — never rename. */
    val token: String,
) {
    Default("default"),
    Montserrat("mont"),
    PTSerif("serif");

    fun label(): String = when (this) {
        Default -> "Aa"
        Montserrat -> "Mnt"
        PTSerif -> "Ser"
    }

    fun displayName(): String = when (this) {
        Default -> "Default"
        Montserrat -> "Montserrat"
        PTSerif -> "PT Serif"
    }

    companion object {
        fun fromToken(token: String): TextFontFamily? = entries.firstOrNull { it.token == token }
    }
}
