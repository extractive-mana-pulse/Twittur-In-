package com.example.twitturin.feature.timetable.presentation.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimetableSubjectPickerViewModel(
    private val repository: TimetableRepository,
) : ViewModel() {

    private val _selectedIds = MutableStateFlow(repository.followedSubjectIds.value)
    private val _searchQuery = MutableStateFlow("")
    private val _cohortQuery = MutableStateFlow("")

    private val _events = Channel<TimetableSubjectPickerEvent>()
    val events = _events.receiveAsFlow()

    // The timetable is observed rather than snapshotted: on a cold start the repository loads its
    // stored snapshot off-disk asynchronously, so reading `.value` at construction can catch a
    // null and leave the picker permanently empty.
    val state: StateFlow<TimetableSubjectPickerState> = combine(
        repository.timetable,
        _selectedIds,
        _searchQuery,
        _cohortQuery,
    ) { timetable, selected, query, cohortQuery -> buildState(timetable, selected, query, cohortQuery) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimetableSubjectPickerState())

    fun onAction(action: TimetableSubjectPickerAction) {
        when (action) {
            is TimetableSubjectPickerAction.OnSearchQueryChange -> _searchQuery.value = action.query
            is TimetableSubjectPickerAction.OnCohortQueryChange -> _cohortQuery.value = action.query
            is TimetableSubjectPickerAction.OnToggleSubject -> _selectedIds.update { current ->
                if (action.subjectId in current) current - action.subjectId else current + action.subjectId
            }
            is TimetableSubjectPickerAction.OnToggleGroup -> _selectedIds.update { current ->
                // Deselect the group only when every row is already selected; otherwise fill it in.
                if (action.subjectIds.all { it in current }) current - action.subjectIds.toSet()
                else current + action.subjectIds
            }
            is TimetableSubjectPickerAction.OnCohortSelected -> {
                val cohortSubjectIds = repository.timetable.value?.cohortSubjectIds?.get(action.cohortId).orEmpty()
                _selectedIds.update { it + cohortSubjectIds }
                _cohortQuery.value = ""
            }
            TimetableSubjectPickerAction.OnSave -> {
                repository.setFollowedSubjects(_selectedIds.value)
                viewModelScope.launch { _events.send(TimetableSubjectPickerEvent.SavedAndClose) }
            }
        }
    }

    private fun buildState(
        timetable: Timetable?,
        selected: Set<String>,
        query: String,
        cohortQuery: String,
    ): TimetableSubjectPickerState {
        val tt = timetable ?: return TimetableSubjectPickerState()
        val followableSubjects = tt.subjects.filter { it.id in tt.followableSubjectIds }
        val filtered = if (query.isBlank()) {
            followableSubjects
        } else {
            followableSubjects.filter { it.name.contains(query, ignoreCase = true) || it.shortName.contains(query, ignoreCase = true) }
        }
        val groups = filtered
            .groupBy { it.baseName }
            .entries
            .sortedBy { it.key }
            .map { (baseName, subjects) ->
                SubjectGroupUi(
                    baseName = baseName,
                    rows = subjects
                        .sortedBy { it.kind.ordinal }
                        .map { subject ->
                            SubjectRowUi(
                                id = subject.id,
                                label = subject.shortName.ifBlank { subject.name },
                                kindLabel = subject.kind.name,
                                checked = subject.id in selected,
                            )
                        },
                )
            }
        val cohortSuggestions = if (cohortQuery.isBlank()) {
            emptyList()
        } else {
            tt.cohorts.filter { it.name.contains(cohortQuery, ignoreCase = true) }
                .take(8)
                .map { TimetableCohortUi(it.id, it.name) }
        }

        return TimetableSubjectPickerState(
            cohortQuery = cohortQuery,
            cohortSuggestions = cohortSuggestions,
            searchQuery = query,
            groups = groups,
            selectedCount = selected.size,
            totalFollowableCount = followableSubjects.size,
        )
    }
}
