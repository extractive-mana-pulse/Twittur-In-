package com.example.twitturin.tweet.presentation.home.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.event.SingleLiveEvent
import com.example.twitturin.tweet.presentation.home.sealed.HomeScreenUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _event = SingleLiveEvent<HomeScreenUiEvent>()
    val event: LiveData<HomeScreenUiEvent> get() = _event

    private fun onAddButtonPressed() {
        _event.value = HomeScreenUiEvent.NavigateToPublicPost
    }

    private fun onDrawerPressed() {
        _event.value = HomeScreenUiEvent.OpenDrawer
    }

    fun sendEvent(event : HomeScreenUiEvent) {
        when(event) {
            is HomeScreenUiEvent.OpenDrawer -> { onDrawerPressed() }
            is HomeScreenUiEvent.NavigateToPublicPost -> { onAddButtonPressed() }
        }
    }
}