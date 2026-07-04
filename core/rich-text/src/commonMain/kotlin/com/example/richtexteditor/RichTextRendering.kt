package com.example.richtexteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import com.example.richtexteditor.resources.Res
import com.example.richtexteditor.resources.montserrat_regular
import com.example.richtexteditor.resources.pt_serif_regular
import com.example.richtexteditor.util.TextFontFamily
import org.jetbrains.compose.resources.Font

/** The editor's two selectable fonts, resolved once from compose resources. */
@Immutable
class RichTextFonts(
    val montserrat: FontFamily,
    val ptSerif: FontFamily,
) {
    /** Null for [TextFontFamily.Default] — the text inherits the app's own font. */
    fun familyFor(font: TextFontFamily): FontFamily? = when (font) {
        TextFontFamily.Default -> null
        TextFontFamily.Montserrat -> montserrat
        TextFontFamily.PTSerif -> ptSerif
    }
}

@Composable
fun rememberRichTextFonts(): RichTextFonts {
    val montserrat = FontFamily(Font(Res.font.montserrat_regular))
    val ptSerif = FontFamily(Font(Res.font.pt_serif_regular))
    return remember(montserrat, ptSerif) { RichTextFonts(montserrat, ptSerif) }
}

/**
 * Styles [text] with [spans] — upstream `RichTextField`'s AnnotatedString builder, extracted so
 * the editor overlay and read-only display share one implementation. Attributes left at their
 * `Default` stay unspecified and inherit from the rendering [androidx.compose.ui.text.TextStyle].
 */
fun buildRichAnnotatedString(
    text: String,
    spans: List<FormattingSpan>,
    fonts: RichTextFonts,
): AnnotatedString = buildAnnotatedString {
    append(text)
    spans.forEach { span ->
        val validStart = maxOf(0, span.start)
        val validEnd = minOf(span.end, text.length)
        if (validStart < validEnd && span.hasFormatting) {
            addStyle(
                style = SpanStyle(
                    fontWeight = if (span.isBold) FontWeight.Bold else null,
                    fontStyle = if (span.isItalic) FontStyle.Italic else null,
                    textDecoration = if (span.isUnderline) TextDecoration.Underline else null,
                    color = span.color.color ?: Color.Unspecified,
                    fontSize = span.fontSize.size ?: TextUnit.Unspecified,
                    fontFamily = fonts.familyFor(span.fontFamily),
                ),
                start = validStart,
                end = validEnd,
            )
        }
    }
}

/** Decode + style a stored wire-format string for read-only display. */
fun renderRichText(encoded: String, fonts: RichTextFonts): AnnotatedString {
    val doc = decodeRichText(encoded)
    return buildRichAnnotatedString(doc.text, doc.spans, fonts)
}

/** Remembered [renderRichText] for direct use in a `Text(...)` call. */
@Composable
fun rememberRichText(encoded: String): AnnotatedString {
    val fonts = rememberRichTextFonts()
    return remember(encoded, fonts) { renderRichText(encoded, fonts) }
}
