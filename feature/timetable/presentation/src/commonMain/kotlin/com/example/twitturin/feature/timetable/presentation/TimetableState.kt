package com.example.twitturin.feature.timetable.presentation

import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.timetable.domain.TimetableRange
import kotlinx.datetime.LocalDate

data class TimetableState(
    val isLoading: Boolean = false,
    val hasTimetable: Boolean = false,
    val followedCount: Int = 0,
    val visibleRange: TimetableRange = TimetableRange.WEEK,
    val isMonthUnlocked: Boolean = false,
    val anchorDate: LocalDate? = null,
    /** Dates currently on screen for Day/3-Day/Week — empty for Month, which renders from
     *  [monthDots] via a calendar grid instead of day columns. */
    val windowDates: List<LocalDate> = emptyList(),
    val blocksByDate: Map<LocalDate, List<TimetableBlockUi>> = emptyMap(),
    /** Month only: date -> number of followed lessons that weekday carries. */
    val monthDots: Map<LocalDate, Int> = emptyMap(),
    val selectedLesson: TimetableBlockUi? = null,
    val sourceFileName: String? = null,
    val error: UiText? = null,
)

/** A single lesson block ready to render — Compose-free like the rest of this app's Ui models;
 *  the composable derives a colour from [subjectId] the same way [GradientAvatar] derives one
 *  from a name (see `subjectColorFor`). */
data class TimetableBlockUi(
    val occurrenceId: String,
    val subjectId: String,
    val subjectName: String,
    val subjectShortName: String,
    val teacherNames: String,
    val roomNames: String,
    val startLabel: String,
    val endLabel: String,
    val startMinutesOfDay: Int,
    val endMinutesOfDay: Int,
    val periodNumber: Int,
)
