package com.example.richtexteditor.helper

import com.example.richtexteditor.FormattingSpan

fun mergeAdjacentSpans(spans: List<FormattingSpan>): List<FormattingSpan> {
    if (spans.isEmpty()) return spans

    val merged = mutableListOf<FormattingSpan>()
    var current = spans[0]

    for (i in 1 until spans.size) {
        val next = spans[i]

        if (current.end == next.start && spansHaveSameFormatting(current, next)) {
            current = current.copy(end = next.end)
        } else {
            merged.add(current)
            current = next
        }
    }

    merged.add(current)

    return merged
}
