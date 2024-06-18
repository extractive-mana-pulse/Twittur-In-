package com.example.twitturin.auth.presentation.stayIn.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.example.twitturin.auth.presentation.stayIn.sealed.StayInUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StayInViewModel(app : Application): AndroidViewModel(app) {

    private val channel = MutableStateFlow<StayInUiEvent>(StayInUiEvent.NothingState)
    val stayInEvent = channel.asStateFlow()

    private fun onSavePressed(){
        channel.value = StayInUiEvent.OnSavePressed
    }

    private fun onFullScreenPressed(){
        channel.value = StayInUiEvent.FullScreenPressed
    }

    private fun onNotSavePressed(){
        channel.value = StayInUiEvent.OnNotSavePressed
    }

    fun stayInUiEvent(event : StayInUiEvent){
        when(event){
            is StayInUiEvent.OnSavePressed -> { onSavePressed() }
            is StayInUiEvent.OnNotSavePressed -> { onNotSavePressed() }
            StayInUiEvent.FullScreenPressed -> { onFullScreenPressed() }
            StayInUiEvent.NothingState -> {  }
        }
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
}