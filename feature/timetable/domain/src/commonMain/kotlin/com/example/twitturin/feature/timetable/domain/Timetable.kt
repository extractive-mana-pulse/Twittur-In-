package com.example.twitturin.feature.timetable.domain

import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant
import kotlinx.datetime.LocalTime

/**
 * A subject/course row from the uploaded timetable. [baseName] and [kind] are derived from the
 * institution's naming convention of suffixing the session type in parentheses, e.g.
 * "Algorithms and Programming I (lec)" → baseName "Algorithms and Programming I", kind LECTURE.
 * [id] is the stable id from the source export and is what a student's "follow" is keyed on.
 */
data class TimetableSubject(
    val id: String,
    val name: String,
    val shortName: String,
    val baseName: String,
    val kind: SubjectKind,
)

enum class SubjectKind { LECTURE, LAB, PRACTICE, SEMINAR, OTHER }

data class TimetableTeacher(val id: String, val name: String, val shortName: String)

data class TimetableRoom(val id: String, val name: String)

/** A student cohort ("class" in the source export, e.g. "IT2-24") — used for the picker's quick-filter. */
data class TimetableCohort(val id: String, val name: String)

data class TimetablePeriod(val number: Int, val start: LocalTime, val end: LocalTime)

/**
 * One recurring weekly slot: a subject taught to some cohorts, at a given day/period.
 * [isInstructional] is false for placeholder blocks with no teacher (e.g. institution-wide
 * holiday/exam-session entries) — these are excluded from the subject-follow picker and rendered
 * as a banner instead of a normal lesson block.
 */
data class TimetableOccurrence(
    val id: String,
    val subjectId: String,
    val teacherIds: List<String>,
    val roomIds: List<String>,
    val cohortIds: List<String>,
    val dayOfWeek: DayOfWeek,
    val period: TimetablePeriod,
    val isInstructional: Boolean,
)

/**
 * WEEKLY_RECURRING: the export defines a single week pattern that repeats indefinitely — Month
 * view would show nothing a Week view doesn't already show, so it stays disabled.
 * MULTI_WEEK: the export actually varies by week (a real term/rotating-schedule export) — Month
 * view is meaningful and gets enabled. See [Timetable.coverage].
 */
enum class TimetableCoverage { WEEKLY_RECURRING, MULTI_WEEK }

data class Timetable(
    val subjects: List<TimetableSubject>,
    val teachers: List<TimetableTeacher>,
    val rooms: List<TimetableRoom>,
    val cohorts: List<TimetableCohort>,
    /** cohortId -> subjectIds taught to it; drives the subject picker's "pick your group" pre-filter. */
    val cohortSubjectIds: Map<String, Set<String>>,
    val occurrences: List<TimetableOccurrence>,
    val coverage: TimetableCoverage,
    val sourceFileName: String,
    val importedAt: Instant,
) {
    /** Subjects worth offering in the follow-picker: backed by at least one real (taught) occurrence. */
    val followableSubjectIds: Set<String> by lazy {
        occurrences.filter { it.isInstructional }.mapTo(mutableSetOf()) { it.subjectId }
    }
}
