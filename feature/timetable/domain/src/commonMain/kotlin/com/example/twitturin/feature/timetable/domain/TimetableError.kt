package com.example.twitturin.feature.timetable.domain

import com.example.twitturin.core.domain.util.Error

/** Import/storage errors specific to the Timetable feature. */
sealed interface TimetableError : Error {
    /** The file isn't well-formed XML. */
    data object MalformedXml : TimetableError

    /** It parsed, but doesn't look like a recognized timetable export (no periods/subjects at all). */
    data object UnsupportedFormat : TimetableError

    /** It parsed and matched the format, but there's nothing scheduled (no periods or no cards). */
    data object EmptyTimetable : TimetableError

    /** Reading/writing the local snapshot failed (disk full, permission denied, etc). */
    data object StorageFailure : TimetableError
}
