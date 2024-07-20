package com.example.twitturin.auth.presentation.kind.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.auth.presentation.kind.sealed.KindUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class KindViewModel : ViewModel() {

    private val channel = Channel<KindUiEvent>(Channel.BUFFERED)
    val kindEventResult = channel.receiveAsFlow()

    fun onBackPressedKind(){ viewModelScope.launch { channel.send(KindUiEvent.OnBackPressed) } }

    fun onProfPressed(){ viewModelScope.launch { channel.send(KindUiEvent.NavigateToProfReg) } }

    fun onStudPressed(){ viewModelScope.launch { channel.send(KindUiEvent.NavigateToStudReg) } }
}