package com.example.twitturin.feature.timetable.domain

/** How much of the timetable is visible at once. MONTH is only selectable when the uploaded
 *  export actually covers more than one distinct week pattern — see [TimetableCoverage]. */
enum class TimetableRange { DAY, THREE_DAY, WEEK, MONTH }
