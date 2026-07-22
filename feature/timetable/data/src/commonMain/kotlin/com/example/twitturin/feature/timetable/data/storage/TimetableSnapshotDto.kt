package com.example.twitturin.feature.timetable.data.storage

import kotlinx.serialization.Serializable

/**
 * JSON mirror of the domain [com.example.twitturin.feature.timetable.domain.Timetable], used
 * only for the local on-disk snapshot (see [TimetableRawFileStore]). Every field is a plain
 * primitive/String/List/Map — deliberately not reusing kotlinx-datetime's own types (`LocalTime`,
 * `Instant`, `DayOfWeek`) here, to avoid depending on exactly how/whether they're wired into this
 * module's `Json` configuration; times are minutes-since-midnight, everything else is a String.
 */
@Serializable
internal data class TimetableSnapshotDto(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val subjects: List<SubjectDto>,
    val teachers: List<TeacherDto>,
    val rooms: List<RoomDto>,
    val cohorts: List<CohortDto>,
    val cohortSubjectIds: Map<String, List<String>>,
    val occurrences: List<OccurrenceDto>,
    val coverage: String,
    val sourceFileName: String,
    val importedAtEpochSeconds: Long,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}

@Serializable
internal data class SubjectDto(
    val id: String,
    val name: String,
    val shortName: String,
    val baseName: String,
    val kind: String,
)

@Serializable
internal data class TeacherDto(val id: String, val name: String, val shortName: String)

@Serializable
internal data class RoomDto(val id: String, val name: String)

@Serializable
internal data class CohortDto(val id: String, val name: String)

@Serializable
internal data class OccurrenceDto(
    val id: String,
    val subjectId: String,
    val teacherIds: List<String>,
    val roomIds: List<String>,
    val cohortIds: List<String>,
    val dayOfWeek: String,
    val periodNumber: Int,
    val periodStartMinutes: Int,
    val periodEndMinutes: Int,
    val isInstructional: Boolean,
)
