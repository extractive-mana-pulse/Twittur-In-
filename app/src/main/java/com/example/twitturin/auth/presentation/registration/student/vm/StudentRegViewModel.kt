package com.example.twitturin.auth.presentation.registration.student.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.auth.domain.model.RegistrationFormState
import com.example.twitturin.auth.domain.use_case.Username
import com.example.twitturin.auth.domain.use_case.ValidateEmail
import com.example.twitturin.auth.presentation.registration.professor.sealed.RegistrationFormEvent
import com.example.twitturin.auth.presentation.registration.student.sealed.StudRegUiEvent
import com.example.twitturin.event.SingleLiveEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StudentRegViewModel(private val validateUsername: Username = Username()): ViewModel() {


    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    var state by mutableStateOf(RegistrationFormState())

    fun onEvent(event: RegistrationFormEvent) {
        when(event) {
            is RegistrationFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }
            is RegistrationFormEvent.Submit -> { submitData() }
        }
    }

    private fun submitData() {
//        val emailResult = validateUsername.execute(state.username,)
//
//        val hasError = listOf(emailResult).any { !it.successful }
//
//        if(hasError) {
//            state = state.copy(emailError = emailResult.errorMessage)
//            return
//        }
//        viewModelScope.launch {
//            validationEventChannel.send(ValidationEvent.Success)
//        }
    }


    private val _studRegEvent = SingleLiveEvent<StudRegUiEvent>()
    val studRegEvent: LiveData<StudRegUiEvent> = _studRegEvent

    private fun onStudRegPressed(){
        _studRegEvent.value = StudRegUiEvent.OnRegPressed
    }

    fun sentStudRegEvent(event: StudRegUiEvent) {
        when(event) {
            is StudRegUiEvent.OnRegPressed -> { onStudRegPressed() }
        }
    }
}

sealed class ValidationEvent { data object Success: ValidationEvent() }