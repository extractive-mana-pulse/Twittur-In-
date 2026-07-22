package com.example.twitturin.feature.timetable.data.parser

import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.timetable.domain.SubjectKind
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableCohort
import com.example.twitturin.feature.timetable.domain.TimetableCoverage
import com.example.twitturin.feature.timetable.domain.TimetableError
import com.example.twitturin.feature.timetable.domain.TimetableOccurrence
import com.example.twitturin.feature.timetable.domain.TimetablePeriod
import com.example.twitturin.feature.timetable.domain.TimetableRoom
import com.example.twitturin.feature.timetable.domain.TimetableSubject
import com.example.twitturin.feature.timetable.domain.TimetableTeacher
import kotlinx.datetime.DayOfWeek
import kotlin.time.Clock

/** Parses raw XML bytes into a domain [Timetable] in one step. */
internal fun parseAscTimetable(bytes: ByteArray, fileName: String): Result<Timetable, TimetableError> =
    AscTimetableXmlParser.parse(bytes).map { raw -> AscTimetableMapper.map(raw, fileName) }

/**
 * Maps the flat aSc rows onto the parser-agnostic domain model. All aSc-specific assumptions
 * (day-bitmask order, the "(lec)/(lab)/.../(mar)" subject-name suffix convention, "no teacher =
 * non-instructional block") live here and in [AscTimetableXmlParser] only — a different export
 * dialect only needs a new mapper, not a change to the domain model or UI.
 */
internal object AscTimetableMapper {

    // Card `days` is a 6-char Mon..Sat bitmask (this institution has no Sunday classes) — matches
    // the source file's own <daysdef> convention (Monday="100000" .. Saturday="000001").
    private val DAY_ORDER = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
    )

    fun map(raw: RawAscTimetable, sourceFileName: String): Timetable {
        val periodsByNumber = raw.periods.associateBy { it.number }
        val lessonsById = raw.lessons.associateBy { it.id }

        val occurrences = buildOccurrences(raw.cards, lessonsById, periodsByNumber)
        val cohortSubjectIds = buildCohortSubjectIds(raw.lessons)
        val coverage = detectCoverage(raw.lessons)

        return Timetable(
            subjects = raw.subjects.map { it.toDomain() },
            teachers = raw.teachers.map { TimetableTeacher(it.id, it.name, it.shortName) },
            rooms = raw.rooms.map { TimetableRoom(it.id, it.name) },
            cohorts = raw.cohorts.map { TimetableCohort(it.id, it.name) },
            cohortSubjectIds = cohortSubjectIds,
            occurrences = occurrences,
            coverage = coverage,
            sourceFileName = sourceFileName,
            importedAt = Clock.System.now(),
        )
    }

    private fun buildOccurrences(
        cards: List<RawCard>,
        lessonsById: Map<String, RawLesson>,
        periodsByNumber: Map<Int, RawPeriod>,
    ): List<TimetableOccurrence> {
        // Keyed by synthesized id to silently de-duplicate any repeated card for the same
        // lesson+period+day (shouldn't happen in valid data, but costs nothing to guard).
        val occurrences = LinkedHashMap<String, TimetableOccurrence>()
        for (card in cards) {
            val lesson = lessonsById[card.lessonId] ?: continue
            val period = periodsByNumber[card.period] ?: continue
            // The card's own classroomids is the room actually assigned to this placed slot. The
            // lesson's classroomids is the (often long) list of *candidate* rooms the scheduler
            // could have used — joining all of those would render a dozen rooms on one block, so
            // only fall back to it when it names a single, unambiguous room.
            val roomIds = card.classroomIds.ifEmpty { lesson.classroomIds.takeIf { it.size == 1 }.orEmpty() }
            for ((index, bit) in card.days.withIndex()) {
                if (bit != '1' || index >= DAY_ORDER.size) continue
                val dayOfWeek = DAY_ORDER[index]
                val id = "${lesson.id}:${period.number}:${dayOfWeek.name}"
                occurrences[id] = TimetableOccurrence(
                    id = id,
                    subjectId = lesson.subjectId,
                    teacherIds = lesson.teacherIds,
                    roomIds = roomIds,
                    cohortIds = lesson.classIds,
                    dayOfWeek = dayOfWeek,
                    period = TimetablePeriod(period.number, period.start, period.end),
                    isInstructional = lesson.teacherIds.isNotEmpty(),
                )
            }
        }
        return occurrences.values.toList()
    }

    private fun buildCohortSubjectIds(lessons: List<RawLesson>): Map<String, Set<String>> {
        val map = mutableMapOf<String, MutableSet<String>>()
        for (lesson in lessons) {
            for (classId in lesson.classIds) {
                map.getOrPut(classId) { mutableSetOf() }.add(lesson.subjectId)
            }
        }
        return map
    }

    /** More than one distinct weeksdefid actually referenced by a lesson means the schedule
     *  varies by week (a real term/rotating export) rather than being one flat recurring week. */
    private fun detectCoverage(lessons: List<RawLesson>): TimetableCoverage {
        val distinctWeeksDefIds = lessons.mapNotNull { it.weeksDefId.ifEmpty { null } }.toSet()
        return if (distinctWeeksDefIds.size > 1) TimetableCoverage.MULTI_WEEK else TimetableCoverage.WEEKLY_RECURRING
    }

    private fun RawSubject.toDomain(): TimetableSubject {
        val match = SUFFIX_PATTERN.find(name)
        val baseName = match?.groupValues?.get(1)?.takeIf { it.isNotBlank() } ?: name
        val kind = when (match?.groupValues?.get(2)?.lowercase()) {
            "lec" -> SubjectKind.LECTURE
            "mar" -> SubjectKind.LECTURE // Uzbek "ma'ruza" = lecture
            "lab" -> SubjectKind.LAB
            "prac" -> SubjectKind.PRACTICE
            "sem" -> SubjectKind.SEMINAR
            else -> SubjectKind.OTHER
        }
        return TimetableSubject(id = id, name = name, shortName = shortName, baseName = baseName, kind = kind)
    }

    private val SUFFIX_PATTERN = Regex("""^(.*?)\s*\(([^()]+)\)\s*$""")
}
