package com.example.twitturin.feature.timetable.data

import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.feature.timetable.data.parser.parseAscTimetable
import com.example.twitturin.feature.timetable.data.storage.TimetableLocalStore
import com.example.twitturin.feature.timetable.data.storage.TimetableRawFileStore
import com.example.twitturin.feature.timetable.data.storage.TimetableSnapshotDto
import com.example.twitturin.feature.timetable.data.storage.toDomainOrNull
import com.example.twitturin.feature.timetable.data.storage.toSnapshotDto
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableError
import com.example.twitturin.feature.timetable.domain.TimetableRange
import com.example.twitturin.feature.timetable.domain.TimetableRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * On-device only — no backend involved. The parsed timetable is cached in memory (loaded once
 * from the local JSON snapshot, if any) and updated on import.
 *
 * Internal: consumers reach this only through the [TimetableRepository] interface, wired by
 * [com.example.twitturin.feature.timetable.data.di.timetableDataModule] within this module.
 */
internal class TimetableRepositoryImpl(
    private val fileStore: TimetableRawFileStore,
    private val localStore: TimetableLocalStore,
) : TimetableRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _timetable = MutableStateFlow<Timetable?>(null)
    override val timetable: StateFlow<Timetable?> = _timetable.asStateFlow()

    private val _followedSubjectIds = MutableStateFlow(localStore.readFollowedSubjectIds())
    override val followedSubjectIds: StateFlow<Set<String>> = _followedSubjectIds.asStateFlow()

    private val _selectedRange = MutableStateFlow(localStore.readSelectedRange())
    override val selectedRange: StateFlow<TimetableRange> = _selectedRange.asStateFlow()

    init {
        scope.launch {
            val stored = fileStore.read() ?: return@launch
            val snapshot = runCatching { json.decodeFromString<TimetableSnapshotDto>(stored) }.getOrNull()
            _timetable.value = snapshot?.toDomainOrNull()
        }
    }

    override suspend fun importTimetable(bytes: ByteArray, fileName: String): Result<Timetable, TimetableError> =
        parseAscTimetable(bytes, fileName).onSuccess { parsed ->
            _timetable.value = parsed
            // The in-memory copy is still usable for this session even if persisting fails
            // (e.g. disk full) — importing isn't rolled back over a storage-only failure.
            runCatching { fileStore.write(json.encodeToString(parsed.toSnapshotDto())) }
        }

    override fun setFollowedSubjects(ids: Set<String>) {
        localStore.writeFollowedSubjectIds(ids)
        _followedSubjectIds.value = ids
    }

    override fun setSelectedRange(range: TimetableRange) {
        localStore.writeSelectedRange(range)
        _selectedRange.value = range
    }

    override suspend fun clearTimetable() {
        fileStore.clear()
        localStore.clear()
        _timetable.value = null
        _followedSubjectIds.value = emptySet()
        _selectedRange.value = TimetableRange.WEEK
    }
}
