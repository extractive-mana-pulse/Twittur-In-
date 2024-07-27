package com.example.twitturin.feedback.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.feedback.presentation.sealed.FeedbackUIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FeedbackViewModel: ViewModel() {

    private val uiEvent = Channel<FeedbackUIEvent>(Channel.BUFFERED)
    val feedbackUiEvent = uiEvent.receiveAsFlow()

    fun onBackPressed() { viewModelScope.launch { uiEvent.send(FeedbackUIEvent.OnBackPressed) } }

    fun onSendPressed() { viewModelScope.launch { uiEvent.send(FeedbackUIEvent.OnSendPressed) } }
}