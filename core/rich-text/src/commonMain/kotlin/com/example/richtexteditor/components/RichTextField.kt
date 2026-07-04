package com.example.richtexteditor.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import com.example.richtexteditor.RichTextEditorAction
import com.example.richtexteditor.RichTextEditorController
import com.example.richtexteditor.buildRichAnnotatedString
import com.example.richtexteditor.helper.applyFormattingToSelection
import com.example.richtexteditor.helper.getCurrentFormatting
import com.example.richtexteditor.helper.handleTextChange
import com.example.richtexteditor.rememberRichTextFonts

/**
 * Upstream's editor field: a [BasicTextField] whose own glyphs are transparent, with the styled
 * [androidx.compose.ui.text.AnnotatedString] drawn as an overlay in the decoration box.
 *
 * Adapted for hosting inside app screens: document state lives in [controller] (not locally),
 * base look comes from [textStyle]/[MaterialTheme] instead of hardcoded fonts and colours, and
 * the host can cap [maxLength] (visible characters) and [maxLines].
 */
@Composable
fun RichTextField(
    controller: RichTextEditorController,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: String = "Start writing...",
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
) {
    val fonts = rememberRichTextFonts()
    var isFocused by remember { mutableStateOf(false) }
    val state = controller.state

    val annotatedText = remember(controller.fieldValue.text, controller.spans, fonts) {
        buildRichAnnotatedString(controller.fieldValue.text, controller.spans, fonts)
    }

    val contentColor = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onSurface }
    // The invisible input text must match the toolbar's current formatting so the cursor
    // tracks the glyphs the overlay draws (upstream technique, themed defaults).
    val baseStyle = textStyle.copy(
        fontSize = state.currentFontSize.size ?: textStyle.fontSize,
        fontFamily = fonts.familyFor(state.currentFontFamily) ?: textStyle.fontFamily,
    )

    BasicTextField(
        value = controller.fieldValue,
        onValueChange = { newValue ->
            if (newValue.text.length > maxLength) return@BasicTextField
            val oldValue = controller.fieldValue
            controller.fieldValue = newValue
            controller.spans = handleTextChange(
                oldText = oldValue.text,
                newText = newValue.text,
                oldSelection = oldValue.selection,
                newSelection = newValue.selection,
                spans = controller.spans,
                currentFormatting = getCurrentFormatting(state),
            )
        },
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        textStyle = baseStyle.copy(color = Color.Transparent),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        maxLines = maxLines,
    ) { innerTextField ->
        Box {
            if (controller.fieldValue.text.isEmpty() && !isFocused) {
                Text(
                    text = placeholder,
                    style = baseStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)),
                )
            }
            if (controller.fieldValue.text.isNotEmpty()) {
                Text(
                    text = annotatedText,
                    style = baseStyle.copy(color = contentColor),
                )
            }
            innerTextField()
        }
    }

    // Collapsed cursor inside styled text → reflect that formatting on the toolbar.
    LaunchedEffect(controller.fieldValue.selection, controller.spans) {
        val fieldValue = controller.fieldValue
        if (fieldValue.selection.collapsed && fieldValue.text.isNotEmpty()) {
            val cursorPos = fieldValue.selection.start
            val spanAtCursor = controller.spans.firstOrNull { cursorPos > it.start && cursorPos <= it.end }
                ?: controller.spans.lastOrNull { cursorPos == it.start }
            if (spanAtCursor != null) {
                controller.onAction(
                    RichTextEditorAction.UpdateToolbarFromCursor(
                        isBold = spanAtCursor.isBold,
                        isItalic = spanAtCursor.isItalic,
                        isUnderline = spanAtCursor.isUnderline,
                        color = spanAtCursor.color,
                        fontFamily = spanAtCursor.fontFamily,
                        fontSize = spanAtCursor.fontSize,
                    )
                )
            }
        }
    }

    // Toolbar changed while a range is selected → restyle the selection.
    LaunchedEffect(
        state.isCurrentlyBold, state.isCurrentlyItalic, state.isCurrentlyUnderline,
        state.currentColor, state.currentFontFamily, state.currentFontSize,
    ) {
        if (!controller.fieldValue.selection.collapsed) {
            controller.spans = applyFormattingToSelection(
                selection = controller.fieldValue.selection,
                spans = controller.spans,
                formatting = getCurrentFormatting(state),
            )
        }
    }
}
