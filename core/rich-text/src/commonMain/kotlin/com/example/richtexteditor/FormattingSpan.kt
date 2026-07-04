package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

data class FormattingSpan(
    val start: Int,
    val end: Int,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val color: TextColor = TextColor.Default,
    val fontFamily: TextFontFamily = TextFontFamily.Default,
    val fontSize: TextSize = TextSize.Default,
) {
    /** True when this span renders any differently from the surrounding plain text. */
    val hasFormatting: Boolean
        get() = isBold || isItalic || isUnderline ||
            color != TextColor.Default ||
            fontFamily != TextFontFamily.Default ||
            fontSize != TextSize.Default
}
