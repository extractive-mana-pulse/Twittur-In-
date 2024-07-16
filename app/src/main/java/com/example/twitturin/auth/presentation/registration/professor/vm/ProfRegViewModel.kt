package com.example.twitturin.auth.presentation.registration.professor.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.auth.presentation.registration.professor.sealed.ProfRegUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfRegViewModel: ViewModel() {

    private val channel = Channel<ProfRegUiEvent>(Channel.BUFFERED)
    val profRegEvent = channel.receiveAsFlow()

    fun onRegPressed(){ viewModelScope.launch { channel.send(ProfRegUiEvent.OnAuthPressed) } }

    fun onBackPressed(){ viewModelScope.launch { channel.send(ProfRegUiEvent.OnBackPressed) } }

}