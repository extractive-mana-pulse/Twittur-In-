package com.example.richtexteditor.helper

import com.example.richtexteditor.FormattingStyle
import com.example.richtexteditor.RichTextEditorState

fun getCurrentFormatting(state: RichTextEditorState): FormattingStyle {
    return FormattingStyle(
        isBold = state.isCurrentlyBold,
        isItalic = state.isCurrentlyItalic,
        isUnderline = state.isCurrentlyUnderline,
        color = state.currentColor,
        fontFamily = state.currentFontFamily,
        fontSize = state.currentFontSize
    )
}
