package com.example.twitturin.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.auth.domain.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank() || _state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.login(username.trim(), password.trim())
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.LoggedIn)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.ShowError(error.toUiText()))
                }
        }
    }
}
