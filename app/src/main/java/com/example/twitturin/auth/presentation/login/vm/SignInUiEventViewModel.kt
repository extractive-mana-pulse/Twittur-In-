package com.example.twitturin.auth.presentation.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInUiEventViewModel @Inject constructor(): ViewModel() {

    private val _signInEvent = SingleLiveEvent<SignInUiEvent>()
    val signInEvent: LiveData<SignInUiEvent> = _signInEvent

    private fun onLoginPressed(){
        _signInEvent.value = SignInUiEvent.OnLoginPressed
    }
    fun sendKindEvents(event: SignInUiEvent) {
        when(event) {
            is SignInUiEvent.OnLoginPressed -> { onLoginPressed() }
        }
    }
}