package com.example.richtexteditor.helper

import com.example.richtexteditor.FormattingSpan
import com.example.richtexteditor.FormattingStyle

fun handleTextInsertion(
    insertPosition: Int,
    insertedLength: Int,
    spans: List<FormattingSpan>,
    currentFormatting: FormattingStyle,
): List<FormattingSpan> {
    val newSpans = mutableListOf<FormattingSpan>()

    spans.forEach { span ->
        when {
            span.end <= insertPosition -> {
                newSpans.add(span)
            }

            span.start < insertPosition && span.end > insertPosition -> {
                newSpans.add(span.copy(end = insertPosition))
                newSpans.add(
                    span.copy(
                        start = insertPosition + insertedLength,
                        end = span.end + insertedLength
                    )
                )
            }

            else -> {
                newSpans.add(
                    span.copy(
                        start = span.start + insertedLength,
                        end = span.end + insertedLength
                    )
                )
            }
        }
    }

    val newSpan = FormattingSpan(
        start = insertPosition,
        end = insertPosition + insertedLength,
        isBold = currentFormatting.isBold,
        isItalic = currentFormatting.isItalic,
        isUnderline = currentFormatting.isUnderline,
        color = currentFormatting.color,
        fontFamily = currentFormatting.fontFamily,
        fontSize = currentFormatting.fontSize
    )
    newSpans.add(newSpan)

    return mergeAdjacentSpans(newSpans.sortedBy { it.start })
}
