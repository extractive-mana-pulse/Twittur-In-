package com.example.twitturin.auth.presentation.registration.student.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.presentation.registration.student.sealed.StudRegUiEvent
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudentRegViewModel @Inject constructor(): ViewModel() {

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