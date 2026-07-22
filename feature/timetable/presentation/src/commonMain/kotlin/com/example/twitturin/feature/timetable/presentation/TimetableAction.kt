package com.example.twitturin.feature.timetable.presentation

import com.example.twitturin.feature.timetable.domain.TimetableRange
import kotlinx.datetime.LocalDate

sealed interface TimetableAction {
    /** Upload/Replace both just trigger the platform picker from the Root; once a file comes
     *  back it arrives as [OnFilePicked]. */
    data object OnUploadClick : TimetableAction
    data object OnReplaceTimetable : TimetableAction
    data class OnFilePicked(val bytes: ByteArray, val fileName: String) : TimetableAction

    data class OnRangeSelect(val range: TimetableRange) : TimetableAction
    data object OnPrevWindow : TimetableAction
    data object OnNextWindow : TimetableAction
    data object OnToday : TimetableAction
    /** Tapping a day in Month view — jumps straight to Day view for that date. */
    data class OnJumpToDate(val date: LocalDate) : TimetableAction

    data object OnOpenSubjectPicker : TimetableAction
    data class OnLessonClick(val occurrenceId: String) : TimetableAction
    data object OnDismissLessonDetail : TimetableAction

    data object OnClearTimetable : TimetableAction
}
