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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.richtexteditor.FormattingSpan
import com.example.richtexteditor.RichTextEditorAction
import com.example.richtexteditor.RichTextEditorController
import com.example.richtexteditor.RichTextFonts
import com.example.richtexteditor.buildRichAnnotatedString
import com.example.richtexteditor.helper.applyFormattingToSelection
import com.example.richtexteditor.helper.getCurrentFormatting
import com.example.richtexteditor.helper.handleTextChange
import com.example.richtexteditor.rememberRichTextFonts

/**
 * The editor field. Upstream drew the styled document as an overlay on a transparent
 * [BasicTextField]; whenever a span's font/size differed from the field's base style the two
 * layouts disagreed and the cursor drifted off the visible glyphs. Here the styling is applied
 * as a [VisualTransformation] instead, so the field lays out (and positions the cursor within)
 * exactly the text the user sees. Character offsets are untouched — [OffsetMapping.Identity].
 *
 * Document state lives in [controller]; base look comes from [textStyle]/[MaterialTheme]; the
 * host can cap [maxLength] (visible characters) and [maxLines].
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

    val visualTransformation = remember(controller.spans, fonts) {
        RichTextVisualTransformation(controller.spans, fonts)
    }

    val contentColor = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onSurface }
    val baseStyle = textStyle.copy(color = contentColor)

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
        textStyle = baseStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        visualTransformation = visualTransformation,
        maxLines = maxLines,
    ) { innerTextField ->
        Box {
            if (controller.fieldValue.text.isEmpty() && !isFocused) {
                Text(
                    text = placeholder,
                    style = baseStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)),
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

/** Applies the document's spans as text-field styling; offsets are 1:1 with the raw text. */
private class RichTextVisualTransformation(
    private val spans: List<FormattingSpan>,
    private val fonts: RichTextFonts,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(
        text = buildRichAnnotatedString(text.text, spans, fonts),
        offsetMapping = OffsetMapping.Identity,
    )
}
