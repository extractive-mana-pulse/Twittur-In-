package com.example.twitturin.auth.presentation.registration.professor.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.presentation.registration.professor.sealed.ProfRegUiEvent
import com.example.twitturin.event.SingleLiveEvent

class ProfRegViewModel: ViewModel() {

    private val _profRegEvent = SingleLiveEvent<ProfRegUiEvent>()
    val profRegEvent: LiveData<ProfRegUiEvent> = _profRegEvent

    private fun onRegPressed(){
        _profRegEvent.value = ProfRegUiEvent.OnAuthPressed
    }
    fun sentProfRegEvent(event: ProfRegUiEvent) {
        when(event) {
            is ProfRegUiEvent.OnAuthPressed -> { onRegPressed() }
        }
    }
}