package com.example.twitturin.feature.timetable.presentation

import com.example.twitturin.core.presentation.UiText

sealed interface TimetableEvent {
    data object OpenSubjectPicker : TimetableEvent
    data class ShowMessage(val message: UiText) : TimetableEvent
}
