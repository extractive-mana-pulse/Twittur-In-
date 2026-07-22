package com.example.twitturin.feature.timetable.data.parser

import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.feature.timetable.domain.TimetableError
import kotlinx.datetime.LocalTime

internal data class RawPeriod(val number: Int, val start: LocalTime, val end: LocalTime)
internal data class RawSubject(val id: String, val name: String, val shortName: String)
internal data class RawTeacher(val id: String, val name: String, val shortName: String)
internal data class RawRoom(val id: String, val name: String)
internal data class RawCohort(val id: String, val name: String)

internal data class RawLesson(
    val id: String,
    val subjectId: String,
    val classIds: List<String>,
    val teacherIds: List<String>,
    val classroomIds: List<String>,
    val weeksDefId: String,
)

internal data class RawCard(val lessonId: String, val period: Int, val days: String, val classroomIds: List<String>)

internal data class RawAscTimetable(
    val periods: List<RawPeriod>,
    val subjects: List<RawSubject>,
    val teachers: List<RawTeacher>,
    val rooms: List<RawRoom>,
    val cohorts: List<RawCohort>,
    val lessons: List<RawLesson>,
    val cards: List<RawCard>,
)

/**
 * Reads the aSc Timetables XML export into flat intermediate rows.
 *
 * Deliberately ignores the `<daysdef>`/`<weeksdef>`/`<termsdef>`/`<group>`/`<grade>`/`<student>`
 * sections: each `<card>` carries its own literal day bitmask (no daysdef lookup needed to place
 * an occurrence), and [AscTimetableMapper]'s coverage detection only needs to count distinct
 * `weeksdefid` values referenced by `<lesson>` rows, not resolve a weeksdef's own bitmask
 * semantics.
 */
internal object AscTimetableXmlParser {

    fun parse(bytes: ByteArray): Result<RawAscTimetable, TimetableError> {
        val text = runCatching { bytes.decodeToString(throwOnInvalidSequence = true) }
            .getOrElse { return Result.Error(TimetableError.MalformedXml) }

        val periods = mutableListOf<RawPeriod>()
        val subjects = mutableListOf<RawSubject>()
        val teachers = mutableListOf<RawTeacher>()
        val rooms = mutableListOf<RawRoom>()
        val cohorts = mutableListOf<RawCohort>()
        val lessons = mutableListOf<RawLesson>()
        val cards = mutableListOf<RawCard>()

        try {
            val reader = XmlElementReader(text)
            while (true) {
                val element = reader.nextElement() ?: break
                when (element.name) {
                    "period" -> parsePeriod(element)?.let { periods += it }

                    "subject" -> element.attr("id")?.let { id ->
                        subjects += RawSubject(id, element.attr("name").orEmpty(), element.attr("short").orEmpty())
                    }

                    "teacher" -> element.attr("id")?.let { id ->
                        teachers += RawTeacher(id, element.attr("name").orEmpty(), element.attr("short").orEmpty())
                    }

                    "classroom" -> element.attr("id")?.let { id ->
                        rooms += RawRoom(id, element.attr("name").orEmpty())
                    }

                    "class" -> element.attr("id")?.let { id ->
                        cohorts += RawCohort(id, element.attr("name").orEmpty())
                    }

                    "lesson" -> element.attr("id")?.let { id ->
                        lessons += RawLesson(
                            id = id,
                            subjectId = element.attr("subjectid").orEmpty(),
                            classIds = element.attrList("classids"),
                            teacherIds = element.attrList("teacherids"),
                            classroomIds = element.attrList("classroomids"),
                            weeksDefId = element.attr("weeksdefid").orEmpty(),
                        )
                    }

                    "card" -> element.attr("lessonid")?.let { lessonId ->
                        val period = element.attr("period")?.toIntOrNull()
                        val days = element.attr("days")
                        if (period != null && !days.isNullOrEmpty()) {
                            cards += RawCard(lessonId, period, days, element.attrList("classroomids"))
                        }
                    }
                }
            }
        } catch (_: Exception) {
            return Result.Error(TimetableError.MalformedXml)
        }

        if (subjects.isEmpty() && periods.isEmpty()) {
            return Result.Error(TimetableError.UnsupportedFormat)
        }
        if (periods.isEmpty() || cards.isEmpty()) {
            return Result.Error(TimetableError.EmptyTimetable)
        }

        return Result.Success(RawAscTimetable(periods, subjects, teachers, rooms, cohorts, lessons, cards))
    }

    private fun parsePeriod(element: XmlElement): RawPeriod? {
        val number = element.attr("period")?.toIntOrNull() ?: return null
        val start = element.attr("starttime")?.let(::parseClockTime) ?: return null
        val end = element.attr("endtime")?.let(::parseClockTime) ?: return null
        return RawPeriod(number, start, end)
    }
}

private fun XmlElement.attr(name: String): String? = attributes[name]?.takeIf { it.isNotEmpty() }

private fun XmlElement.attrList(name: String): List<String> =
    attributes[name].orEmpty().split(',').map { it.trim() }.filter { it.isNotEmpty() }

/** aSc times are written like "9:00" / "14:20" — no leading zero on the hour. */
private fun parseClockTime(raw: String): LocalTime? {
    val parts = raw.split(':')
    if (parts.size != 2) return null
    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    return runCatching { LocalTime(hour, minute) }.getOrNull()
}
