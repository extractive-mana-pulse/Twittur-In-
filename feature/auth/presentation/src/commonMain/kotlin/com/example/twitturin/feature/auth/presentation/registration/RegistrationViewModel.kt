package com.example.twitturin.feature.auth.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.auth.domain.AuthRepository
import com.example.twitturin.feature.auth.domain.ProfessorRegistration
import com.example.twitturin.feature.auth.domain.StudentRegistration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared by the student + professor sign-up screens — both POST to the same `users` endpoint. */
class RegistrationViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegistrationEvent>()
    val events = _events.receiveAsFlow()

    fun registerStudent(fullName: String, username: String, studentId: String, major: String, password: String) {
        submit {
            authRepository.registerStudent(
                StudentRegistration(
                    fullName = fullName.trim(),
                    username = username.trim(),
                    studentId = studentId.trim(),
                    major = major,
                    password = password.trim(),
                ),
            )
        }
    }

    fun registerProfessor(fullName: String, username: String, subject: String, password: String) {
        submit {
            authRepository.registerProfessor(
                ProfessorRegistration(
                    fullName = fullName.trim(),
                    username = username.trim(),
                    subject = subject.trim(),
                    password = password.trim(),
                ),
            )
        }
    }

    private fun submit(call: suspend () -> EmptyResult<DataError.Network>) {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            call()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegistrationEvent.Registered)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegistrationEvent.ShowError(error.toUiText()))
                }
        }
    }
}
