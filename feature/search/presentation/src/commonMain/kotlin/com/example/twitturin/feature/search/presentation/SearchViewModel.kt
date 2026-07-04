package com.example.twitturin.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.search.domain.SearchRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChange -> _state.update { it.copy(query = action.query) }
            is SearchAction.OnSearch -> search(action.query)
            is SearchAction.OnUserClick -> viewModelScope.launch {
                _events.send(SearchEvent.NavigateToProfile(action.userId))
            }
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasSearched = true) }
            searchRepository.searchUsers(query.trim())
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            results = users.map { user -> user.toSearchUserUi() },
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SearchEvent.ShowError(error.toUiText()))
                }
        }
    }
}
