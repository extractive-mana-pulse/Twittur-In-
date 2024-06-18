package com.example.twitturin.auth.presentation.registration.professor.vm

import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.presentation.registration.professor.sealed.ProfRegUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfRegViewModel: ViewModel() {

    private val channel = MutableStateFlow<ProfRegUiEvent>(ProfRegUiEvent.NothingState)
    val profRegEvent = channel.asStateFlow()

    private fun onRegPressed(){
        channel.value = ProfRegUiEvent.OnAuthPressed
    }

    private fun onBackPressed(){
        channel.value = ProfRegUiEvent.OnBackPressed
    }

    fun sentProfRegEvent(event: ProfRegUiEvent) {
        when(event) {
            is ProfRegUiEvent.OnAuthPressed -> { onRegPressed() }
            ProfRegUiEvent.NothingState -> {  }
            ProfRegUiEvent.OnBackPressed -> { onBackPressed() }
        }
    }
}