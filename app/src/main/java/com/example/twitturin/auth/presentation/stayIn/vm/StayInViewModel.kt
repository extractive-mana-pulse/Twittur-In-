package com.example.twitturin.auth.presentation.stayIn.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.example.twitturin.auth.presentation.stayIn.sealed.StayInUiEvent
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StayInViewModel @Inject constructor(app : Application): AndroidViewModel(app) {

    private val _stayInEvent = SingleLiveEvent<StayInUiEvent>()
    val stayInEvent: LiveData<StayInUiEvent> = _stayInEvent

    private fun onSavePressed(){
        _stayInEvent.value = StayInUiEvent.OnSavePressed
    }

    private fun onNotSavePressed(){
        _stayInEvent.value = StayInUiEvent.OnNotSavePressed
    }

    fun stayInUiEvent(event : StayInUiEvent){
        when(event){
            is StayInUiEvent.OnSavePressed -> { onSavePressed() }
            is StayInUiEvent.OnNotSavePressed -> { onNotSavePressed() }
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