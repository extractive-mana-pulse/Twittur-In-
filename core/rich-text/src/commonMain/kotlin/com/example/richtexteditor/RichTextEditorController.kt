package com.example.richtexteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

/**
 * State holder for one rich-text document plus its toolbar.
 *
 * Replaces upstream's `RichTextEditorViewModel` (androidx-lifecycle, toolbar state only) and the
 * field state upstream kept inside `RichTextField` — hoisting both lets a host screen read the
 * encoded document to submit it, prefill it in edit mode, and clear it after posting. The
 * [onAction] reducer is the upstream ViewModel's `when` block verbatim.
 */
class RichTextEditorController(initialEncoded: String = "") {

    var fieldValue by mutableStateOf(TextFieldValue(""))
    var spans by mutableStateOf<List<FormattingSpan>>(emptyList())
    var state by mutableStateOf(RichTextEditorState())
        private set

    init {
        if (initialEncoded.isNotEmpty()) setEncoded(initialEncoded)
    }

    /** The visible text, without wire-format markup — use for length counters and blank checks. */
    val plainText: String get() = fieldValue.text

    /** Serialize the document to the wire format (plain text when nothing is formatted). */
    fun encode(): String = encodeRichText(fieldValue.text, spans)

    /** Replace the whole document, e.g. when entering edit mode with a stored tweet. */
    fun setEncoded(encoded: String) {
        val doc = decodeRichText(encoded)
        fieldValue = TextFieldValue(doc.text, selection = TextRange(doc.text.length))
        spans = doc.spans
    }

    fun clear() {
        fieldValue = TextFieldValue("")
        spans = emptyList()
        state = RichTextEditorState()
    }

    fun onAction(action: RichTextEditorAction) {
        state = when (action) {
            RichTextEditorAction.OnBoldClick ->
                state.copy(isCurrentlyBold = !state.isCurrentlyBold)

            RichTextEditorAction.OnItalicClick ->
                state.copy(isCurrentlyItalic = !state.isCurrentlyItalic)

            RichTextEditorAction.OnUnderlineClick ->
                state.copy(isCurrentlyUnderline = !state.isCurrentlyUnderline)

            RichTextEditorAction.OnColorClick ->
                state.copy(isSelectColorDropdownExpanded = true)

            RichTextEditorAction.OnColorDropdownDismiss ->
                state.copy(isSelectColorDropdownExpanded = false)

            is RichTextEditorAction.OnColorChange ->
                state.copy(currentColor = action.value, isSelectColorDropdownExpanded = false)

            RichTextEditorAction.OnFontFamilyClick ->
                state.copy(isSelectFontFamilyDropdownExpanded = true)

            RichTextEditorAction.OnFontFamilyDropdownDismiss ->
                state.copy(isSelectFontFamilyDropdownExpanded = false)

            is RichTextEditorAction.OnFontFamilyChange ->
                state.copy(currentFontFamily = action.value, isSelectFontFamilyDropdownExpanded = false)

            RichTextEditorAction.OnFontSizeClick ->
                state.copy(isSelectFontSizeDropdownExpanded = true)

            RichTextEditorAction.OnFontSizeDropdownDismiss ->
                state.copy(isSelectFontSizeDropdownExpanded = false)

            is RichTextEditorAction.OnFontSizeChange ->
                state.copy(currentFontSize = action.value, isSelectFontSizeDropdownExpanded = false)

            RichTextEditorAction.OnResetClick ->
                state.copy(
                    isCurrentlyBold = false,
                    isCurrentlyItalic = false,
                    isCurrentlyUnderline = false,
                    currentColor = TextColor.Default,
                    currentFontFamily = TextFontFamily.Default,
                    currentFontSize = TextSize.Default,
                )

            is RichTextEditorAction.UpdateToolbarFromCursor ->
                state.copy(
                    isCurrentlyBold = action.isBold,
                    isCurrentlyItalic = action.isItalic,
                    isCurrentlyUnderline = action.isUnderline,
                    currentColor = action.color,
                    currentFontFamily = action.fontFamily,
                    currentFontSize = action.fontSize,
                )
        }
    }

    companion object {
        /** Survives config change/process death by round-tripping the wire format (cursor excluded). */
        val Saver: Saver<RichTextEditorController, String> = Saver(
            save = { it.encode() },
            restore = { RichTextEditorController(it) },
        )
    }
}

@Composable
fun rememberRichTextEditorController(initialEncoded: String = ""): RichTextEditorController =
    rememberSaveable(initialEncoded, saver = RichTextEditorController.Saver) {
        RichTextEditorController(initialEncoded)
    }
