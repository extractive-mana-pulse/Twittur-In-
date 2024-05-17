package com.example.twitturin.follow.presentation.followers.vm

import androidx.lifecycle.ViewModel
import com.example.twitturin.follow.presentation.followers.sealed.FollowersUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FollowersUiViewModel : ViewModel() {

    private val channel = MutableStateFlow<FollowersUiEvent>(FollowersUiEvent.NothingState)
    val followersEvent = channel.asStateFlow()

    private fun itemPressed() { channel.value = FollowersUiEvent.OnItemPressed }

    private fun followPressed() {

    }


    fun sendUiEvent(event : FollowersUiEvent) {
        when(event) {
            is FollowersUiEvent.NothingState -> {  }
            is FollowersUiEvent.OnItemPressed -> { itemPressed() }
            is FollowersUiEvent.OnFollowPressed -> { followPressed() }
        }
    }
}