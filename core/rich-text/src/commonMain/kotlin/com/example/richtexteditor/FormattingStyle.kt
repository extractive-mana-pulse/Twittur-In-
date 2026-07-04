package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

data class FormattingStyle(
    val isBold: Boolean,
    val isItalic: Boolean,
    val isUnderline: Boolean,
    val color: TextColor,
    val fontFamily: TextFontFamily,
    val fontSize: TextSize,
)
