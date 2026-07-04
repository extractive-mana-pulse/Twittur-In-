package com.example.richtexteditor.util

import androidx.compose.ui.graphics.Color

/**
 * Text colours offered by the editor. Upstream's fixed palette is kept verbatim; [Default]
 * (a Twittur addition) means "inherit the surrounding text colour", so unstyled text follows
 * the app theme in both light and dark mode.
 */
enum class TextColor(
    val color: Color?,
    /** Stable token used by the wire format — never rename. */
    val token: String,
) {
    Default(null, "default"),
    PureWhite(Color(0xffFFFFFF), "white"),
    SoftCream(Color(0xffFFF2E9), "cream"),
    WarmBeige(Color(0xffF7E6C7), "beige"),
    DeepRed(Color(0xff943112), "red"),
    MidnightBlue(Color(0xff0D1040), "navy"),
    Black(Color(0xff000000), "black");

    companion object {
        fun fromToken(token: String): TextColor? = entries.firstOrNull { it.token == token }
    }
}
