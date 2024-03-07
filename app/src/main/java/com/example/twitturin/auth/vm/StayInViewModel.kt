package com.example.twitturin.auth.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.example.twitturin.auth.sealed.StayIn
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class StayInViewModel @Inject constructor(app : Application) : AndroidViewModel(app) {

    private val _stayIn = SingleLiveEvent<StayIn>()
    val stayIn: SingleLiveEvent<StayIn> = _stayIn

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
}
