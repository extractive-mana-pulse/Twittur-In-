package com.example.twitturin.feature.timetable.data.parser

internal data class XmlElement(val name: String, val attributes: Map<String, String>)

/**
 * Minimal hand-rolled element reader for the aSc Timetables XML export format.
 *
 * The export is only two levels deep — a root wrapping section containers (`<subjects>`,
 * `<teachers>`, ...), each holding flat, self-closing, attribute-only leaf rows (`<subject .../>`,
 * `<teacher .../>`, ...). There's no text content between tags and no CDATA; checked against a
 * real ~200KB export, the only entity used in practice is `&amp;`. That's well within what a
 * small hand-written reader handles correctly, so this avoids taking on a third-party
 * multiplatform XML dependency (whose iOS/native support upstream itself flags as beta quality)
 * for a format this constrained. If a future export needs real nested/mixed content, revisit
 * that trade-off.
 */
internal class XmlElementReader(private val xml: String) {
    private var pos = 0
    private val length = xml.length

    /** Returns the next open or self-closing element in document order, or null at end of input.
     *  Closing tags, comments, and processing instructions are skipped transparently. */
    fun nextElement(): XmlElement? {
        while (true) {
            val lt = xml.indexOf('<', pos)
            if (lt < 0) return null
            pos = lt
            when {
                xml.startsWith("<?", pos) -> skipUntilAfter("?>")
                xml.startsWith("<!--", pos) -> skipUntilAfter("-->")
                xml.startsWith("<!", pos) -> skipUntilAfter(">")
                xml.startsWith("</", pos) -> skipUntilAfter(">")
                else -> return readTag()
            }
        }
    }

    private fun skipUntilAfter(marker: String) {
        val end = xml.indexOf(marker, pos)
        pos = if (end < 0) length else end + marker.length
    }

    private fun readTag(): XmlElement {
        pos++ // consume '<'
        val nameStart = pos
        while (pos < length && !xml[pos].isNameBoundary()) pos++
        val name = xml.substring(nameStart, pos)

        val attributes = LinkedHashMap<String, String>()
        while (pos < length) {
            skipWhitespace()
            if (pos >= length) break
            val guard = pos

            if (xml[pos] == '/' && pos + 1 < length && xml[pos + 1] == '>') {
                pos += 2
                break
            }
            if (xml[pos] == '>') {
                pos += 1
                break
            }

            val attrNameStart = pos
            while (pos < length && xml[pos] != '=' && xml[pos] != '>' && xml[pos] != '/' && !xml[pos].isWhitespace()) pos++
            val attrName = xml.substring(attrNameStart, pos)
            skipWhitespace()
            if (pos < length && xml[pos] == '=') {
                pos++
                skipWhitespace()
                if (pos < length && (xml[pos] == '"' || xml[pos] == '\'')) {
                    val quote = xml[pos]
                    pos++
                    val valueStart = pos
                    while (pos < length && xml[pos] != quote) pos++
                    val raw = xml.substring(valueStart, pos)
                    if (pos < length) pos++ // closing quote
                    if (attrName.isNotEmpty()) attributes[attrName] = decodeXmlEntities(raw)
                }
            }

            if (pos == guard) pos++ // guarantee forward progress on any malformed input
        }
        return XmlElement(name, attributes)
    }

    private fun skipWhitespace() {
        while (pos < length && xml[pos].isWhitespace()) pos++
    }

    private fun Char.isNameBoundary() = isWhitespace() || this == '/' || this == '>'
}

internal fun decodeXmlEntities(value: String): String {
    if ('&' !in value) return value
    val sb = StringBuilder(value.length)
    var i = 0
    while (i < value.length) {
        val c = value[i]
        if (c == '&') {
            val semi = value.indexOf(';', i + 1)
            if (semi > i) {
                val decoded = decodeEntity(value.substring(i + 1, semi))
                if (decoded != null) {
                    sb.append(decoded)
                    i = semi + 1
                    continue
                }
            }
        }
        sb.append(c)
        i++
    }
    return sb.toString()
}

private fun decodeEntity(entity: String): String? = when {
    entity == "amp" -> "&"
    entity == "lt" -> "<"
    entity == "gt" -> ">"
    entity == "quot" -> "\""
    entity == "apos" -> "'"
    entity.startsWith("#x") || entity.startsWith("#X") ->
        entity.drop(2).toIntOrNull(16)?.takeIf { it in 0..0xFFFF }?.let { it.toChar().toString() }
    entity.startsWith("#") ->
        entity.drop(1).toIntOrNull()?.takeIf { it in 0..0xFFFF }?.let { it.toChar().toString() }
    else -> null
}
