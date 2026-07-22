package com.example.twitturin.feature.timetable.presentation

import com.example.twitturin.feature.timetable.domain.TimetableRange
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Pure date-window math shared by the ViewModel and previews/tests. The underlying data has no
 * absolute dates (a weekly-recurring export is just day-of-week + period), so "what's on date X"
 * is always resolved by matching X's weekday against occurrences — these functions only decide
 * *which calendar dates* are currently on screen, not what's scheduled on them.
 */
internal object TimetableWindowing {

    /** Week always starts Monday; this data never has Sunday lessons, so Week shows Mon..Sat (6
     *  columns) rather than a conventional 7-day week. Month returns empty — it renders from a
     *  full calendar grid (see [monthDates]) instead of a list of day columns. */
    fun windowDates(range: TimetableRange, anchor: LocalDate): List<LocalDate> = when (range) {
        TimetableRange.DAY -> listOf(anchor)
        TimetableRange.THREE_DAY -> List(3) { anchor.plus(it, DateTimeUnit.DAY) }
        TimetableRange.WEEK -> {
            // DayOfWeek.ordinal is 0 for MONDAY..6 for SUNDAY (matches the mapper's own DAY_ORDER),
            // so +1 gives the same 1-indexed ISO day number without depending on a named property
            // that isn't available on every kotlinx-datetime platform target.
            val monday = anchor.minus(anchor.dayOfWeek.ordinal, DateTimeUnit.DAY)
            List(6) { monday.plus(it, DateTimeUnit.DAY) }
        }
        TimetableRange.MONTH -> emptyList()
    }

    /** Every calendar date in the month containing [anchor], in order. */
    fun monthDates(anchor: LocalDate): List<LocalDate> {
        val firstOfMonth = LocalDate(anchor.year, anchor.month, 1)
        val daysInMonth = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
        return List(daysInMonth) { firstOfMonth.plus(it, DateTimeUnit.DAY) }
    }

    /** How far [TimetableAction.OnPrevWindow]/[TimetableAction.OnNextWindow] move the anchor for
     *  the given range — a full week (not just the 6 visible days) so Week stays Monday-aligned. */
    fun shiftAnchor(anchor: LocalDate, range: TimetableRange, direction: Int): LocalDate = when (range) {
        TimetableRange.DAY -> anchor.plus(direction, DateTimeUnit.DAY)
        TimetableRange.THREE_DAY -> anchor.plus(direction * 3, DateTimeUnit.DAY)
        TimetableRange.WEEK -> anchor.plus(direction * 7, DateTimeUnit.DAY)
        TimetableRange.MONTH -> anchor.plus(direction, DateTimeUnit.MONTH)
    }
}
