package com.example.twitturin.auth.presentation.login.vm

import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.domain.model.AuthUser
import com.example.twitturin.auth.presentation.login.sealed.SignIn
import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent
import com.example.twitturin.profile.domain.model.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SignInUiEventViewModel : ViewModel() {

    private val channel = MutableStateFlow<SignInUiEvent>(SignInUiEvent.StateNoting)
    val signInEvent = channel.asStateFlow()

    private fun onLoginPressed() {
        channel.value = SignInUiEvent.OnLoginPressed
    }

    private fun onKindPressed(){
        channel.value = SignInUiEvent.OnKindPressed
    }

    fun sendKindEvents(event: SignInUiEvent) {
        when(event) {
            is SignInUiEvent.OnLoginPressed -> { onLoginPressed() }
            is SignInUiEvent.OnKindPressed -> { onKindPressed() }
            is SignInUiEvent.StateNoting -> {  }
        }
    }
}