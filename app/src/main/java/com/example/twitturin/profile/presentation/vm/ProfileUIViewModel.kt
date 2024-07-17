package com.example.twitturin.profile.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.profile.presentation.sealed.ProfileUIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileUIViewModel: ViewModel() {

    private val uiEvent = Channel<ProfileUIEvent>(Channel.BUFFERED)
    val profileUiEvent = uiEvent.receiveAsFlow()

    fun onBackPressed() { viewModelScope.launch { uiEvent.send(ProfileUIEvent.OnBackPressed) } }

    fun onAvatarPressed() { viewModelScope.launch { uiEvent.send(ProfileUIEvent.OnAvatarPressed) } }

    fun onFollowersPressed() { viewModelScope.launch { uiEvent.send(ProfileUIEvent.OnFollowersPressed) } }

    fun onFollowingPressed() { viewModelScope.launch { uiEvent.send(ProfileUIEvent.OnFollowingPressed) } }
}