package com.example.twitturin.feature.timetable.presentation.picker

sealed interface TimetableSubjectPickerAction {
    data class OnSearchQueryChange(val query: String) : TimetableSubjectPickerAction
    data class OnToggleSubject(val subjectId: String) : TimetableSubjectPickerAction
    /** Select-all / deselect-all for one baseName group: [subjectIds] are the (currently visible) rows under it. */
    data class OnToggleGroup(val subjectIds: List<String>) : TimetableSubjectPickerAction
    data class OnCohortQueryChange(val query: String) : TimetableSubjectPickerAction
    data class OnCohortSelected(val cohortId: String) : TimetableSubjectPickerAction
    data object OnSave : TimetableSubjectPickerAction
}
