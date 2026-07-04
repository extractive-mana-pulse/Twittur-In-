package com.example.richtexteditor.helper

import com.example.richtexteditor.FormattingSpan

fun handleTextDeletion(
    deleteStart: Int,
    deletedLength: Int,
    spans: List<FormattingSpan>
): List<FormattingSpan> {
    val deleteEnd = deleteStart + deletedLength
    val newSpans = mutableListOf<FormattingSpan>()

    spans.forEach { span ->
        when {
            span.end <= deleteStart -> {
                newSpans.add(span)
            }

            span.start >= deleteEnd -> {
                newSpans.add(
                    span.copy(
                        start = span.start - deletedLength,
                        end = span.end - deletedLength
                    )
                )
            }

            else -> {
                val newStart = minOf(span.start, deleteStart)
                val newEnd = maxOf(deleteStart, span.end - deletedLength)

                if (newStart < newEnd) {
                    newSpans.add(
                        span.copy(
                            start = newStart,
                            end = newEnd
                        )
                    )
                }
            }
        }
    }

    return mergeAdjacentSpans(newSpans.sortedBy { it.start })
}
