package com.example.twitturin.feature.timetable.presentation

import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.timetable.domain.TimetableError

fun TimetableError.toUiText(): UiText = UiText.DynamicString(
    when (this) {
        TimetableError.MalformedXml -> "That file doesn't look like valid XML."
        TimetableError.UnsupportedFormat -> "That doesn't look like a timetable export we recognize."
        TimetableError.EmptyTimetable -> "That file parsed, but nothing is scheduled in it."
        TimetableError.StorageFailure -> "Couldn't save the timetable on this device."
    },
)
