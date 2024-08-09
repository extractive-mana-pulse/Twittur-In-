package com.example.twitturin.follow.presentation.followers.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.follow.presentation.followers.sealed.FollowersUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FollowersUiViewModel : ViewModel() {

    private val channel = Channel<FollowersUiEvent>(Channel.BUFFERED)
    val followersEvent = channel.receiveAsFlow()

    fun itemPressed() { viewModelScope.launch { channel.send(FollowersUiEvent.OnItemPressed) } }
    fun followPressed() { viewModelScope.launch { channel.send(FollowersUiEvent.OnFollowPressed) } }
}