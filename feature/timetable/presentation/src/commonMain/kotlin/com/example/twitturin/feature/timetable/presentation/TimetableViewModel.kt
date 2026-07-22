package com.example.twitturin.feature.timetable.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableCoverage
import com.example.twitturin.feature.timetable.domain.TimetableOccurrence
import com.example.twitturin.feature.timetable.domain.TimetableRange
import com.example.twitturin.feature.timetable.domain.TimetableRepository
import com.example.twitturin.feature.timetable.domain.TimetableRoom
import com.example.twitturin.feature.timetable.domain.TimetableSubject
import com.example.twitturin.feature.timetable.domain.TimetableTeacher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TimetableViewModel(
    private val repository: TimetableRepository,
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _anchorDate = MutableStateFlow(today)
    private val _isLoading = MutableStateFlow(false)
    private val _selectedLesson = MutableStateFlow<TimetableBlockUi?>(null)
    private val _error = MutableStateFlow<UiText?>(null)

    private val _events = Channel<TimetableEvent>()
    val events = _events.receiveAsFlow()

    val state: StateFlow<TimetableState> = combine(
        repository.timetable,
        repository.followedSubjectIds,
        repository.selectedRange,
        _anchorDate,
    ) { timetable, followedIds, range, anchor -> buildState(timetable, followedIds, range, anchor) }
        .combine(_isLoading) { state, loading -> state.copy(isLoading = loading) }
        .combine(_selectedLesson) { state, lesson -> state.copy(selectedLesson = lesson) }
        .combine(_error) { state, error -> state.copy(error = error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimetableState())

    fun onAction(action: TimetableAction) {
        when (action) {
            TimetableAction.OnUploadClick, TimetableAction.OnReplaceTimetable -> Unit // Root launches the platform picker directly
            is TimetableAction.OnFilePicked -> importTimetable(action.bytes, action.fileName)
            is TimetableAction.OnRangeSelect -> onRangeSelect(action)
            TimetableAction.OnPrevWindow -> shiftAnchor(-1)
            TimetableAction.OnNextWindow -> shiftAnchor(1)
            TimetableAction.OnToday -> _anchorDate.value = today
            is TimetableAction.OnJumpToDate -> {
                _anchorDate.value = action.date
                repository.setSelectedRange(TimetableRange.DAY)
            }
            TimetableAction.OnOpenSubjectPicker -> viewModelScope.launch { _events.send(TimetableEvent.OpenSubjectPicker) }
            is TimetableAction.OnLessonClick -> onLessonClick(action.occurrenceId)
            TimetableAction.OnDismissLessonDetail -> _selectedLesson.value = null
            TimetableAction.OnClearTimetable -> clearTimetable()
        }
    }

    private fun importTimetable(bytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.importTimetable(bytes, fileName)
                .onSuccess {
                    _anchorDate.value = today
                    _events.send(TimetableEvent.ShowMessage(UiText.DynamicString("Timetable imported")))
                }
                .onFailure { error -> _error.value = error.toUiText() }
            _isLoading.value = false
        }
    }

    private fun onRangeSelect(action: TimetableAction.OnRangeSelect) {
        if (action.range == TimetableRange.MONTH && !state.value.isMonthUnlocked) return
        repository.setSelectedRange(action.range)
    }

    private fun shiftAnchor(direction: Int) {
        _anchorDate.value = TimetableWindowing.shiftAnchor(_anchorDate.value, state.value.visibleRange, direction)
    }

    private fun onLessonClick(occurrenceId: String) {
        _selectedLesson.value = state.value.blocksByDate.values.flatten().firstOrNull { it.occurrenceId == occurrenceId }
    }

    private fun clearTimetable() {
        viewModelScope.launch {
            repository.clearTimetable()
            _anchorDate.value = today
            _selectedLesson.value = null
        }
    }

    private fun buildState(
        timetable: Timetable?,
        followedIds: Set<String>,
        range: TimetableRange,
        anchor: LocalDate,
    ): TimetableState {
        if (timetable == null) {
            return TimetableState(hasTimetable = false, visibleRange = range)
        }

        val subjectsById = timetable.subjects.associateBy(TimetableSubject::id)
        val teachersById = timetable.teachers.associateBy(TimetableTeacher::id)
        val roomsById = timetable.rooms.associateBy(TimetableRoom::id)
        val followedOccurrences = timetable.occurrences.filter { it.isInstructional && it.subjectId in followedIds }

        val windowDates = TimetableWindowing.windowDates(range, anchor)
        val blocksByDate = windowDates.associateWith { date ->
            followedOccurrences
                .filter { it.dayOfWeek == date.dayOfWeek }
                .sortedBy { it.period.number }
                .map { it.toBlockUi(subjectsById, teachersById, roomsById) }
        }
        val monthDots = if (range == TimetableRange.MONTH) {
            TimetableWindowing.monthDates(anchor).associateWith { date ->
                followedOccurrences.count { it.dayOfWeek == date.dayOfWeek }
            }
        } else {
            emptyMap()
        }

        return TimetableState(
            hasTimetable = true,
            followedCount = followedIds.size,
            visibleRange = range,
            isMonthUnlocked = timetable.coverage == TimetableCoverage.MULTI_WEEK,
            anchorDate = anchor,
            windowDates = windowDates,
            blocksByDate = blocksByDate,
            monthDots = monthDots,
            sourceFileName = timetable.sourceFileName,
        )
    }

    private fun TimetableOccurrence.toBlockUi(
        subjectsById: Map<String, TimetableSubject>,
        teachersById: Map<String, TimetableTeacher>,
        roomsById: Map<String, TimetableRoom>,
    ): TimetableBlockUi {
        val subject = subjectsById[subjectId]
        return TimetableBlockUi(
            occurrenceId = id,
            subjectId = subjectId,
            subjectName = subject?.name ?: subjectId,
            subjectShortName = subject?.shortName?.takeIf { it.isNotBlank() } ?: subject?.name ?: subjectId,
            teacherNames = teacherIds.mapNotNull { teachersById[it]?.name }.joinToString(", "),
            roomNames = roomIds.mapNotNull { roomsById[it]?.name }.joinToString(", "),
            startLabel = period.start.toClockLabel(),
            endLabel = period.end.toClockLabel(),
            startMinutesOfDay = period.start.hour * 60 + period.start.minute,
            endMinutesOfDay = period.end.hour * 60 + period.end.minute,
            periodNumber = period.number,
        )
    }
}

private fun LocalTime.toClockLabel(): String = "$hour:${minute.toString().padStart(2, '0')}"
