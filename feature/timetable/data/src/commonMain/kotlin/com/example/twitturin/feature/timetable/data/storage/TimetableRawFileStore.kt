package com.example.twitturin.feature.timetable.data.storage

/**
 * Persists the parsed timetable snapshot's JSON to a real file. `Settings` (SharedPreferences /
 * NSUserDefaults / java.util.prefs.Preferences) is what backs the rest of this app's local
 * prefs, but all three are documented as unsuitable for a payload this size — an institution's
 * full subject/teacher/room catalog comfortably runs past what any of them handle well (desktop's
 * `java.util.prefs` in particular caps a single value around 8KB). See [read]/[write]/[clear].
 *
 * No constructor is declared here on purpose: each platform's `actual` constructs the file
 * location however makes sense for it (Android needs a `Context`, iOS/desktop don't), so this is
 * only ever instantiated from platform-specific Koin wiring (`di/TimetableStorageModule.*.kt`),
 * never from common code.
 */
expect class TimetableRawFileStore {
    suspend fun write(json: String)
    suspend fun read(): String?
    suspend fun clear()
}

internal const val TIMETABLE_SNAPSHOT_FILE_NAME = "timetable_snapshot.json"
