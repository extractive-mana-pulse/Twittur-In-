package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

sealed interface RichTextEditorAction {
    data object OnBoldClick : RichTextEditorAction
    data object OnItalicClick : RichTextEditorAction
    data object OnUnderlineClick : RichTextEditorAction
    data object OnColorClick : RichTextEditorAction
    data object OnColorDropdownDismiss : RichTextEditorAction
    data class OnColorChange(val value: TextColor) : RichTextEditorAction
    data object OnFontFamilyClick : RichTextEditorAction
    data object OnFontFamilyDropdownDismiss : RichTextEditorAction
    data class OnFontFamilyChange(val value: TextFontFamily) : RichTextEditorAction
    data object OnFontSizeClick : RichTextEditorAction
    data object OnFontSizeDropdownDismiss : RichTextEditorAction
    data class OnFontSizeChange(val value: TextSize) : RichTextEditorAction
    data object OnResetClick : RichTextEditorAction
    data class UpdateToolbarFromCursor(
        val isBold: Boolean,
        val isItalic: Boolean,
        val isUnderline: Boolean,
        val color: TextColor,
        val fontFamily: TextFontFamily,
        val fontSize: TextSize,
    ) : RichTextEditorAction
}
