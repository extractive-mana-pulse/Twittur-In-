package com.example.twitturin.feature.timetable.presentation.picker

data class TimetableSubjectPickerState(
    val cohortQuery: String = "",
    val cohortSuggestions: List<TimetableCohortUi> = emptyList(),
    val searchQuery: String = "",
    val groups: List<SubjectGroupUi> = emptyList(),
    val selectedCount: Int = 0,
    val totalFollowableCount: Int = 0,
)

data class TimetableCohortUi(val id: String, val name: String)

/** Subjects sharing a base name grouped together, e.g. "Algorithms and Programming I" ->
 *  [Lecture, Practice] as two checkable rows — mirrors how the source export models a course's
 *  lecture/lab/practice sections as separate followable subjects. */
data class SubjectGroupUi(val baseName: String, val rows: List<SubjectRowUi>)

data class SubjectRowUi(val id: String, val label: String, val kindLabel: String, val checked: Boolean)
