package com.example.twitturin.follow.presentation.following.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.follow.presentation.following.sealed.FollowingUIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FollowingUiViewModel: ViewModel() {

    private val _channel = Channel<FollowingUIEvent>(Channel.BUFFERED)
    val channel =  _channel.receiveAsFlow()

    fun onItemPressed() { viewModelScope.launch { _channel.send(FollowingUIEvent.OnItemPressed) } }

    fun onUnfollowPressed() { viewModelScope.launch { _channel.send(FollowingUIEvent.OnUnFollowPressed) } }
}