package com.example.twitturin.home.presentation.preferences

import android.content.Context
import android.preference.PreferenceManager

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "DARK_STATUS"
        private const val LABEL_STATUS = "LABEL_STATUS"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context!!)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()


    var labelStatus = preferences.getInt(LABEL_STATUS, 0)
        set(value) = preferences.edit().putInt(LABEL_STATUS, value).apply()

}