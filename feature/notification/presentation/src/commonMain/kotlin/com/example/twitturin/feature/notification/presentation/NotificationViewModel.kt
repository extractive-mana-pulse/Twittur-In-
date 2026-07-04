package com.example.twitturin.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.notification.domain.NotificationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state
        .onStart { loadRelease() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = NotificationState(),
        )

    private val _events = Channel<NotificationEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: NotificationAction) {
        when (action) {
            NotificationAction.OnRefresh -> loadRelease()
            NotificationAction.OnPatchNoteClick -> viewModelScope.launch {
                _events.send(NotificationEvent.NavigateToPatchNote)
            }
        }
    }

    private fun loadRelease() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            notificationRepository.getLatestRelease()
                .onSuccess { release ->
                    _state.update { it.copy(isLoading = false, release = release.toReleaseUi()) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(NotificationEvent.ShowError(error.toUiText()))
                }
        }
    }
}
