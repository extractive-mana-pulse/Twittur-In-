package com.example.twitturin.notification.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.notification.presentation.sealed.NotificationUIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotificationViewModel: ViewModel() {

    private val uiEvent = Channel<NotificationUIEvent>(Channel.BUFFERED)
    val notificationEvent = uiEvent.receiveAsFlow()

    fun onDownloadPressed(){
        viewModelScope.launch {
            uiEvent.send(NotificationUIEvent.OnDownloadPressed)
        }
    }

    fun onItemPressed(){
        viewModelScope.launch {
            uiEvent.send(NotificationUIEvent.OnItemPressed)
        }
    }
}