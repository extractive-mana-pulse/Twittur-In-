package com.example.twitturin.home.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.home.presentation.sealed.HomeUIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _event = Channel<HomeUIEvent>(Channel.BUFFERED)
    val homeEvent = _event.receiveAsFlow()

    fun onAddButtonPressed() { viewModelScope.launch { _event.send(HomeUIEvent.NavigateToPublicPost) } }

    fun onDrawerPressed() { viewModelScope.launch { _event.send(HomeUIEvent.OpenDrawer) } }
}