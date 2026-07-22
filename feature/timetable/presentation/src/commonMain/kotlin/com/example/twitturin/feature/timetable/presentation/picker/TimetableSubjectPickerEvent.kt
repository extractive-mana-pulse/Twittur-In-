package com.example.twitturin.feature.timetable.presentation.picker

sealed interface TimetableSubjectPickerEvent {
    data object SavedAndClose : TimetableSubjectPickerEvent
}
