package com.example.richtexteditor

import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RichTextCodecTest {

    @Test
    fun unformatted_document_encodes_byte_identical() {
        val text = "just a plain tweet with <brackets> & ampersands"
        val spans = listOf(FormattingSpan(0, text.length))
        assertEquals(text, encodeRichText(text, spans))
    }

    @Test
    fun legacy_plain_text_decodes_unchanged() {
        val legacy = "old tweet, 2 < 3 && 4 > 1"
        val doc = decodeRichText(legacy)
        assertEquals(legacy, doc.text)
        assertTrue(doc.spans.all { !it.hasFormatting })
    }

    @Test
    fun plain_text_containing_rt_lookalike_is_not_parsed() {
        val tricky = "i heart <rtx> and <rt unclosed"
        val doc = decodeRichText(tricky)
        assertEquals(tricky, doc.text)
        assertTrue(doc.spans.all { !it.hasFormatting })
    }

    @Test
    fun bold_run_round_trips() {
        val text = "hello bold world"
        val spans = listOf(
            FormattingSpan(0, 6),
            FormattingSpan(6, 10, isBold = true),
            FormattingSpan(10, 16),
        )
        val encoded = encodeRichText(text, spans)
        assertEquals("hello <rt b>bold</rt> world", encoded)

        val doc = decodeRichText(encoded)
        assertEquals(text, doc.text)
        val bold = doc.spans.single { it.hasFormatting }
        assertEquals(6, bold.start)
        assertEquals(10, bold.end)
        assertTrue(bold.isBold)
    }

    @Test
    fun all_attributes_round_trip() {
        val text = "styled"
        val span = FormattingSpan(
            start = 0,
            end = 6,
            isBold = true,
            isItalic = true,
            isUnderline = true,
            color = TextColor.DeepRed,
            fontFamily = TextFontFamily.PTSerif,
            fontSize = TextSize.Large,
        )
        val encoded = encodeRichText(text, listOf(span))
        assertEquals("<rt b i u c=red f=serif s=lg>styled</rt>", encoded)

        val decoded = decodeRichText(encoded).spans.single()
        assertEquals(span, decoded)
    }

    @Test
    fun special_characters_survive_a_formatted_round_trip() {
        val text = "a < b & <rt fake> stays text"
        val spans = listOf(FormattingSpan(0, 1, isItalic = true), FormattingSpan(1, text.length))
        val doc = decodeRichText(encodeRichText(text, spans))
        assertEquals(text, doc.text)
        assertTrue(doc.spans.first().isItalic)
    }

    @Test
    fun unknown_attribute_tokens_are_ignored() {
        val doc = decodeRichText("<rt b glow=99 x>hi</rt>")
        val span = doc.spans.single()
        assertEquals("hi", doc.text)
        assertTrue(span.isBold)
        assertEquals(TextColor.Default, span.color)
    }

    @Test
    fun decode_fills_gaps_so_the_whole_text_is_span_covered() {
        val doc = decodeRichText("plain <rt b>bold</rt> tail")
        assertEquals("plain bold tail", doc.text)
        // Full coverage: selection formatting in the editor only restyles covered text.
        assertEquals(0, doc.spans.first().start)
        assertEquals(doc.text.length, doc.spans.last().end)
        var pos = 0
        doc.spans.forEach { span ->
            assertEquals(pos, span.start)
            pos = span.end
        }
    }

    @Test
    fun multiple_formatted_runs_round_trip_in_order() {
        val text = "one two three"
        val spans = listOf(
            FormattingSpan(0, 3, isBold = true),
            FormattingSpan(3, 4),
            FormattingSpan(4, 7, isItalic = true, color = TextColor.MidnightBlue),
            FormattingSpan(7, 8),
            FormattingSpan(8, 13, fontSize = TextSize.Small),
        )
        val doc = decodeRichText(encodeRichText(text, spans))
        assertEquals(text, doc.text)
        assertEquals(spans, doc.spans)
    }

    @Test
    fun empty_document_round_trips() {
        assertEquals("", encodeRichText("", emptyList()))
        val doc = decodeRichText("")
        assertEquals("", doc.text)
        assertTrue(doc.spans.isEmpty())
    }
}
