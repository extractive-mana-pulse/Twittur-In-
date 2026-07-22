package com.example.twitturin.feature.timetable.domain

import com.example.twitturin.core.domain.util.Result
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for the on-device Timetable feature: no backend involved — a student uploads an XML
 * export, it's parsed once and cached locally, and the student narrows it to the subjects they
 * actually attend. Implemented in :feature:timetable:data.
 */
interface TimetableRepository {
    val timetable: StateFlow<Timetable?>
    val followedSubjectIds: StateFlow<Set<String>>
    val selectedRange: StateFlow<TimetableRange>

    /** Parses [bytes], and on success persists it as the new local snapshot (replacing any previous one). */
    suspend fun importTimetable(bytes: ByteArray, fileName: String): Result<Timetable, TimetableError>

    fun setFollowedSubjects(ids: Set<String>)
    fun setSelectedRange(range: TimetableRange)
    suspend fun clearTimetable()
}
