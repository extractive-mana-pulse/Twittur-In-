package com.example.twitturin.feature.timetable.data.storage

import com.example.twitturin.feature.timetable.domain.TimetableRange
import com.russhwolf.settings.Settings

/**
 * Small on-device prefs for the Timetable feature — followed subject ids and the chosen range.
 * Feature-local (not part of :core:domain's `AppPreferences`) since nothing outside this feature
 * needs them. Subject ids are the source export's hex ids, which never contain the delimiter.
 * The parsed timetable itself is far too large for `Settings` — see [TimetableRawFileStore].
 */
internal class TimetableLocalStore(private val settings: Settings) {

    fun readFollowedSubjectIds(): Set<String> =
        settings.getStringOrNull(KEY_FOLLOWED_SUBJECTS)
            ?.split(DELIMITER)
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()

    fun writeFollowedSubjectIds(ids: Set<String>) {
        settings.putString(KEY_FOLLOWED_SUBJECTS, ids.joinToString(DELIMITER))
    }

    fun readSelectedRange(): TimetableRange =
        settings.getStringOrNull(KEY_SELECTED_RANGE)
            ?.let { stored -> runCatching { TimetableRange.valueOf(stored) }.getOrNull() }
            ?: TimetableRange.WEEK

    fun writeSelectedRange(range: TimetableRange) {
        settings.putString(KEY_SELECTED_RANGE, range.name)
    }

    fun clear() {
        settings.remove(KEY_FOLLOWED_SUBJECTS)
        settings.remove(KEY_SELECTED_RANGE)
    }

    private companion object {
        const val KEY_FOLLOWED_SUBJECTS = "timetable_followed_subject_ids"
        const val KEY_SELECTED_RANGE = "timetable_selected_range"
        const val DELIMITER = ","
    }
}
