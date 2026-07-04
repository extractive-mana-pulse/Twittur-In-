package com.example.richtexteditor.helper

import androidx.compose.ui.text.TextRange
import com.example.richtexteditor.FormattingSpan
import com.example.richtexteditor.FormattingStyle

fun applyFormattingToSelection(
    selection: TextRange,
    spans: List<FormattingSpan>,
    formatting: FormattingStyle
): List<FormattingSpan> {
    val selectionStart = selection.min
    val selectionEnd = selection.max
    val newSpans = mutableListOf<FormattingSpan>()

    spans.forEach { span ->
        when {
            span.end <= selectionStart || span.start >= selectionEnd -> {
                newSpans.add(span)
            }

            span.start >= selectionStart && span.end <= selectionEnd -> {
                newSpans.add(
                    span.copy(
                        isBold = formatting.isBold,
                        isItalic = formatting.isItalic,
                        isUnderline = formatting.isUnderline,
                        color = formatting.color,
                        fontFamily = formatting.fontFamily,
                        fontSize = formatting.fontSize
                    )
                )
            }

            else -> {
                if (span.start < selectionStart) {
                    newSpans.add(span.copy(end = selectionStart))
                }

                val overlapStart = maxOf(span.start, selectionStart)
                val overlapEnd = minOf(span.end, selectionEnd)
                newSpans.add(
                    span.copy(
                        start = overlapStart,
                        end = overlapEnd,
                        isBold = formatting.isBold,
                        isItalic = formatting.isItalic,
                        isUnderline = formatting.isUnderline,
                        color = formatting.color,
                        fontFamily = formatting.fontFamily,
                        fontSize = formatting.fontSize
                    )
                )

                if (span.end > selectionEnd) {
                    newSpans.add(span.copy(start = selectionEnd))
                }
            }
        }
    }

    return mergeAdjacentSpans(newSpans.sortedBy { it.start })
}
