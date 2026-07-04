package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.designsystem.theme.SurfaceMuted

/**
 * Minimal CommonMark renderer for Compose Multiplatform — pure commonMain, so it renders
 * identically on Android, iOS, Desktop and Web (no platform `WebView`/Markwon).
 * Supports: ATX headings, bullet/ordered lists, block quotes, fenced & inline code,
 * horizontal rules, and inline **bold** / *italic* / `code` / [links](url).
 */
@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val blocks = remember(markdown) { parseMarkdown(markdown) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block -> MarkdownBlock(block) }
    }
}

@Composable
private fun MarkdownBlock(block: MdBlock) {
    when (block) {
        is MdBlock.Heading -> Text(
            text = inlineMarkdown(block.text),
            style = when (block.level) {
                1 -> MaterialTheme.typography.headlineSmall
                2 -> MaterialTheme.typography.titleLarge
                3 -> MaterialTheme.typography.titleMedium
                else -> MaterialTheme.typography.titleSmall
            },
        )

        is MdBlock.Paragraph -> Text(
            text = inlineMarkdown(block.text),
            style = MaterialTheme.typography.bodyLarge,
        )

        is MdBlock.Bullet -> Row(modifier = Modifier.fillMaxWidth()) {
            Text("•", style = MaterialTheme.typography.bodyLarge, color = Brand)
            Spacer8()
            Text(inlineMarkdown(block.text), style = MaterialTheme.typography.bodyLarge)
        }

        is MdBlock.Ordered -> Row(modifier = Modifier.fillMaxWidth()) {
            Text("${block.number}.", style = MaterialTheme.typography.bodyLarge, color = Brand, fontWeight = FontWeight.Bold)
            Spacer8()
            Text(inlineMarkdown(block.text), style = MaterialTheme.typography.bodyLarge)
        }

        is MdBlock.Quote -> Text(
            text = inlineMarkdown(block.text),
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceMuted, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )

        is MdBlock.Code -> Text(
            text = block.text,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceMuted, RoundedCornerShape(10.dp))
                .padding(12.dp),
        )

        MdBlock.Rule -> HorizontalDivider()
    }
}

@Composable
private fun Spacer8() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
}

// ── inline ─────────────────────────────────────────────────────────────────
@Composable
private fun inlineMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    val n = text.length
    while (i < n) {
        when {
            text.startsWith("**", i) || text.startsWith("__", i) -> {
                val marker = text.substring(i, i + 2)
                val end = text.indexOf(marker, i + 2)
                if (end > i) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text.substring(i + 2, end)) }
                    i = end + 2
                } else { append(text[i]); i++ }
            }

            text[i] == '`' -> {
                val end = text.indexOf('`', i + 1)
                if (end > i) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = SurfaceMuted)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }

            (text[i] == '*' || text[i] == '_') -> {
                val marker = text[i]
                val end = text.indexOf(marker, i + 1)
                // require non-empty content and avoid mid-word underscores (snake_case)
                if (end > i + 1 && !(marker == '_' && i > 0 && text[i - 1].isLetterOrDigit())) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(text.substring(i + 1, end)) }
                    i = end + 1
                } else { append(text[i]); i++ }
            }

            text[i] == '[' -> {
                val close = text.indexOf(']', i + 1)
                val open = if (close > 0) close + 1 else -1
                if (close > i && open in 0 until n && text[open] == '(') {
                    val urlEnd = text.indexOf(')', open + 1)
                    if (urlEnd > open) {
                        val label = text.substring(i + 1, close)
                        val url = text.substring(open + 1, urlEnd)
                        withLink(
                            LinkAnnotation.Url(
                                url = url,
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = Brand, textDecoration = TextDecoration.Underline),
                                ),
                            ),
                        ) { append(label) }
                        i = urlEnd + 1
                    } else { append(text[i]); i++ }
                } else { append(text[i]); i++ }
            }

            else -> { append(text[i]); i++ }
        }
    }
}

// ── block parsing ────────────────────────────────────────────────────────────
private sealed interface MdBlock {
    data class Heading(val level: Int, val text: String) : MdBlock
    data class Paragraph(val text: String) : MdBlock
    data class Bullet(val text: String) : MdBlock
    data class Ordered(val number: Int, val text: String) : MdBlock
    data class Quote(val text: String) : MdBlock
    data class Code(val text: String) : MdBlock
    data object Rule : MdBlock
}

private val ORDERED = Regex("""^(\d+)\.\s+(.*)""")

private fun parseMarkdown(src: String): List<MdBlock> {
    val lines = src.replace("\r\n", "\n").replace("\r", "\n").split("\n")
    val blocks = mutableListOf<MdBlock>()
    val paragraph = StringBuilder()

    fun flushParagraph() {
        if (paragraph.isNotEmpty()) {
            blocks += MdBlock.Paragraph(paragraph.toString().trim())
            paragraph.clear()
        }
    }

    var i = 0
    while (i < lines.size) {
        val raw = lines[i]
        val line = raw.trimEnd()
        val trimmed = line.trimStart()

        when {
            trimmed.startsWith("```") -> {
                flushParagraph()
                val code = StringBuilder()
                i++
                while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                    code.appendLine(lines[i])
                    i++
                }
                blocks += MdBlock.Code(code.toString().trimEnd('\n'))
            }

            line.isBlank() -> flushParagraph()

            trimmed.startsWith("#") -> {
                flushParagraph()
                val level = trimmed.takeWhile { it == '#' }.length.coerceAtMost(6)
                blocks += MdBlock.Heading(level, trimmed.drop(level).trim())
            }

            trimmed == "---" || trimmed == "***" || trimmed == "___" -> {
                flushParagraph()
                blocks += MdBlock.Rule
            }

            trimmed.startsWith("> ") || trimmed == ">" -> {
                flushParagraph()
                blocks += MdBlock.Quote(trimmed.removePrefix(">").trim())
            }

            trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") -> {
                flushParagraph()
                blocks += MdBlock.Bullet(trimmed.drop(2).trim())
            }

            ORDERED.matches(trimmed) -> {
                flushParagraph()
                val m = ORDERED.find(trimmed)!!
                blocks += MdBlock.Ordered(m.groupValues[1].toIntOrNull() ?: 1, m.groupValues[2].trim())
            }

            else -> {
                if (paragraph.isNotEmpty()) paragraph.append(' ')
                paragraph.append(trimmed)
            }
        }
        i++
    }
    flushParagraph()
    return blocks
}
