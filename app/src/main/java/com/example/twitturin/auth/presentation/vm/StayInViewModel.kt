package com.example.twitturin.auth.presentation.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import javax.inject.Inject

class StayInViewModel @Inject constructor(app : Application) : AndroidViewModel(app) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
}
