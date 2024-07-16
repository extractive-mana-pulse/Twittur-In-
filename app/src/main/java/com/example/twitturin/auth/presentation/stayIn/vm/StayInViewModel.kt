package com.example.twitturin.auth.presentation.stayIn.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.twitturin.auth.presentation.stayIn.sealed.StayInUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StayInViewModel(app : Application): AndroidViewModel(app) {

    private val channel = Channel<StayInUiEvent>(Channel.BUFFERED)
    val stayInEvent = channel.receiveAsFlow()

    fun onSavePressed() { viewModelScope.launch { channel.send(StayInUiEvent.OnSavePressed) } }

    fun onFullScreenPressed() { viewModelScope.launch { channel.send(StayInUiEvent.FullScreenPressed) } }

    fun onNotSavePressed() { viewModelScope.launch { channel.send(StayInUiEvent.OnNotSavePressed) } }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    fun isUserLoggedIn(): Boolean { return sharedPreferences.getBoolean("is_logged_in", false) }

    fun setUserLoggedIn(loggedIn: Boolean) { sharedPreferences.edit().putBoolean("is_logged_in", loggedIn).apply() }
}