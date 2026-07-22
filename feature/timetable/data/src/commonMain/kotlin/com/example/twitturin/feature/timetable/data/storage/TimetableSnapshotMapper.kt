package com.example.twitturin.feature.timetable.data.storage

import com.example.twitturin.feature.timetable.domain.SubjectKind
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableCohort
import com.example.twitturin.feature.timetable.domain.TimetableCoverage
import com.example.twitturin.feature.timetable.domain.TimetableOccurrence
import com.example.twitturin.feature.timetable.domain.TimetablePeriod
import com.example.twitturin.feature.timetable.domain.TimetableRoom
import com.example.twitturin.feature.timetable.domain.TimetableSubject
import com.example.twitturin.feature.timetable.domain.TimetableTeacher
import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant
import kotlinx.datetime.LocalTime

internal fun Timetable.toSnapshotDto(): TimetableSnapshotDto = TimetableSnapshotDto(
    subjects = subjects.map { SubjectDto(it.id, it.name, it.shortName, it.baseName, it.kind.name) },
    teachers = teachers.map { TeacherDto(it.id, it.name, it.shortName) },
    rooms = rooms.map { RoomDto(it.id, it.name) },
    cohorts = cohorts.map { CohortDto(it.id, it.name) },
    cohortSubjectIds = cohortSubjectIds.mapValues { (_, ids) -> ids.toList() },
    occurrences = occurrences.map { it.toDto() },
    coverage = coverage.name,
    sourceFileName = sourceFileName,
    importedAtEpochSeconds = importedAt.epochSeconds,
)

private fun TimetableOccurrence.toDto(): OccurrenceDto = OccurrenceDto(
    id = id,
    subjectId = subjectId,
    teacherIds = teacherIds,
    roomIds = roomIds,
    cohortIds = cohortIds,
    dayOfWeek = dayOfWeek.name,
    periodNumber = period.number,
    periodStartMinutes = period.start.hour * 60 + period.start.minute,
    periodEndMinutes = period.end.hour * 60 + period.end.minute,
    isInstructional = isInstructional,
)

/** Null if the stored snapshot doesn't match the schema this build understands (a future app
 *  version changed the shape), or is otherwise corrupt — the caller treats that like "no
 *  timetable yet" rather than crashing on a locally-cached file. */
internal fun TimetableSnapshotDto.toDomainOrNull(): Timetable? {
    if (schemaVersion != TimetableSnapshotDto.CURRENT_SCHEMA_VERSION) return null
    return runCatching {
        Timetable(
            subjects = subjects.map { TimetableSubject(it.id, it.name, it.shortName, it.baseName, SubjectKind.valueOf(it.kind)) },
            teachers = teachers.map { TimetableTeacher(it.id, it.name, it.shortName) },
            rooms = rooms.map { TimetableRoom(it.id, it.name) },
            cohorts = cohorts.map { TimetableCohort(it.id, it.name) },
            cohortSubjectIds = cohortSubjectIds.mapValues { (_, ids) -> ids.toSet() },
            occurrences = occurrences.map { it.toDomain() },
            coverage = TimetableCoverage.valueOf(coverage),
            sourceFileName = sourceFileName,
            importedAt = Instant.fromEpochSeconds(importedAtEpochSeconds),
        )
    }.getOrNull()
}

private fun OccurrenceDto.toDomain(): TimetableOccurrence = TimetableOccurrence(
    id = id,
    subjectId = subjectId,
    teacherIds = teacherIds,
    roomIds = roomIds,
    cohortIds = cohortIds,
    dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
    period = TimetablePeriod(periodNumber, minutesToLocalTime(periodStartMinutes), minutesToLocalTime(periodEndMinutes)),
    isInstructional = isInstructional,
)

private fun minutesToLocalTime(totalMinutes: Int): LocalTime = LocalTime(totalMinutes / 60, totalMinutes % 60)
