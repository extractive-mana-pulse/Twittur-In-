package com.example.twitturin.auth.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInUiEventViewModel : ViewModel() {

    private val _signInEventChannel = Channel<SignInUiEvent>(Channel.BUFFERED)
    val signInEvent = _signInEventChannel.receiveAsFlow()

    fun onLoginPressed() {
        viewModelScope.launch {
            _signInEventChannel.send(SignInUiEvent.OnLoginPressed)
        }
    }

    fun onKindPressed() {
        viewModelScope.launch {
            _signInEventChannel.send(SignInUiEvent.OnKindPressed)
        }
    }
}