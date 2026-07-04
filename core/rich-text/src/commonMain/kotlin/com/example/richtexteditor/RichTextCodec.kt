package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

/**
 * Wire format for rich text (Twittur addition — upstream keeps documents in memory only).
 *
 * The backend stores tweet content as a plain string, so formatting is serialized inline:
 * each formatted run becomes `<rt attrs>text</rt>` where attrs are space-separated tokens —
 * `b`, `i`, `u`, `c=<color>`, `f=<font>`, `s=<size>` (token values live on the enums).
 *
 * Degradation rules, in both directions:
 *  - A document with NO formatting encodes byte-identical to its plain text.
 *  - A string with no well-formed `<rt …>…</rt>` markup decodes as plain text unchanged,
 *    so tweets posted by legacy clients render exactly as before.
 *  - `&`/`<` are escaped (`&amp;`/`&lt;`) only inside formatted documents.
 *  - Unknown attribute tokens are ignored on decode (forward compatibility).
 */
data class RichTextDocument(val text: String, val spans: List<FormattingSpan>)

private const val TAG_PREFIX = "<rt"
private const val TAG_CLOSE = "</rt>"

fun encodeRichText(text: String, spans: List<FormattingSpan>): String {
    val formatted = spans
        .asSequence()
        .map { it.copy(start = it.start.coerceIn(0, text.length), end = it.end.coerceIn(0, text.length)) }
        .filter { it.hasFormatting && it.start < it.end }
        .sortedBy { it.start }
        .toList()
    if (formatted.isEmpty()) return text

    val sb = StringBuilder()
    var pos = 0
    formatted.forEach { span ->
        // Spans are non-overlapping by construction (see helper/); drop any that regress.
        if (span.start < pos) return@forEach
        sb.append(escape(text.substring(pos, span.start)))
        sb.append(TAG_PREFIX)
        if (span.isBold) sb.append(" b")
        if (span.isItalic) sb.append(" i")
        if (span.isUnderline) sb.append(" u")
        if (span.color != TextColor.Default) sb.append(" c=").append(span.color.token)
        if (span.fontFamily != TextFontFamily.Default) sb.append(" f=").append(span.fontFamily.token)
        if (span.fontSize != TextSize.Default) sb.append(" s=").append(span.fontSize.token)
        sb.append('>')
        sb.append(escape(text.substring(span.start, span.end)))
        sb.append(TAG_CLOSE)
        pos = span.end
    }
    sb.append(escape(text.substring(pos)))
    return sb.toString()
}

fun decodeRichText(encoded: String): RichTextDocument {
    if (!encoded.contains(TAG_PREFIX)) return plainDocument(encoded)

    val text = StringBuilder()
    val spans = mutableListOf<FormattingSpan>()
    var sawMarkup = false
    var i = 0
    while (i < encoded.length) {
        val open = encoded.indexOf(TAG_PREFIX, i)
        if (open < 0) {
            text.append(unescape(encoded.substring(i)))
            break
        }
        val attrEnd = encoded.indexOf('>', open)
        val close = if (attrEnd < 0) -1 else encoded.indexOf(TAG_CLOSE, attrEnd + 1)
        val nextCh = encoded.getOrNull(open + TAG_PREFIX.length)
        val wellFormed = attrEnd >= 0 && close >= 0 && (nextCh == ' ' || nextCh == '>')
        if (!wellFormed) {
            // Not our markup ("<rtx…", unclosed tag, …) — keep it as literal text and move on.
            text.append(unescape(encoded.substring(i, open + TAG_PREFIX.length)))
            i = open + TAG_PREFIX.length
            continue
        }
        sawMarkup = true
        text.append(unescape(encoded.substring(i, open)))
        val start = text.length
        text.append(unescape(encoded.substring(attrEnd + 1, close)))
        val span = parseAttrs(encoded.substring(open + TAG_PREFIX.length, attrEnd), start, text.length)
        if (span.start < span.end) spans.add(span)
        i = close + TAG_CLOSE.length
    }

    if (!sawMarkup) return plainDocument(encoded)
    return RichTextDocument(text.toString(), fillGaps(text.toString(), spans))
}

/** The whole string as one unformatted span (full coverage keeps selection formatting working). */
private fun plainDocument(text: String): RichTextDocument = RichTextDocument(
    text = text,
    spans = if (text.isEmpty()) emptyList() else listOf(FormattingSpan(0, text.length)),
)

/**
 * The editor's selection helpers only restyle text that is covered by a span, so decode must
 * cover unformatted stretches with default spans too.
 */
private fun fillGaps(text: String, spans: List<FormattingSpan>): List<FormattingSpan> {
    val result = mutableListOf<FormattingSpan>()
    var pos = 0
    spans.sortedBy { it.start }.forEach { span ->
        if (span.start > pos) result.add(FormattingSpan(pos, span.start))
        result.add(span)
        pos = maxOf(pos, span.end)
    }
    if (pos < text.length) result.add(FormattingSpan(pos, text.length))
    return result
}

private fun parseAttrs(attrs: String, start: Int, end: Int): FormattingSpan {
    var span = FormattingSpan(start = start, end = end)
    attrs.split(' ').forEach { raw ->
        val token = raw.trim()
        when {
            token.isEmpty() -> Unit
            token == "b" -> span = span.copy(isBold = true)
            token == "i" -> span = span.copy(isItalic = true)
            token == "u" -> span = span.copy(isUnderline = true)
            token.startsWith("c=") ->
                TextColor.fromToken(token.removePrefix("c="))?.let { span = span.copy(color = it) }
            token.startsWith("f=") ->
                TextFontFamily.fromToken(token.removePrefix("f="))?.let { span = span.copy(fontFamily = it) }
            token.startsWith("s=") ->
                TextSize.fromToken(token.removePrefix("s="))?.let { span = span.copy(fontSize = it) }
        }
    }
    return span
}

private fun escape(s: String): String = s.replace("&", "&amp;").replace("<", "&lt;")

private fun unescape(s: String): String = s.replace("&lt;", "<").replace("&amp;", "&")
