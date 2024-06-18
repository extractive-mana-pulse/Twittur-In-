package com.example.twitturin.auth.presentation.registration.student.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.auth.presentation.registration.student.sealed.StudentRegistrationUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StudentRegistrationViewModel: ViewModel() {

    private val channel = Channel<StudentRegistrationUiEvent>(Channel.BUFFERED)
    val validationEvents = channel.receiveAsFlow()

    fun onStudentRegistrationClicked(){
        viewModelScope.launch {
            channel.send(StudentRegistrationUiEvent.OnRegisterPressed)
        }
    }

    fun onStudentBackPressed(){
        viewModelScope.launch {
            channel.send(StudentRegistrationUiEvent.OnBackPressed)
        }
    }
}