package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

data class RichTextEditorState(
    val isCurrentlyBold: Boolean = false,
    val isCurrentlyItalic: Boolean = false,
    val isCurrentlyUnderline: Boolean = false,
    val currentColor: TextColor = TextColor.Default,
    val currentFontFamily: TextFontFamily = TextFontFamily.Default,
    val currentFontSize: TextSize = TextSize.Default,
    val isSelectColorDropdownExpanded: Boolean = false,
    val isSelectFontFamilyDropdownExpanded: Boolean = false,
    val isSelectFontSizeDropdownExpanded: Boolean = false,
)
