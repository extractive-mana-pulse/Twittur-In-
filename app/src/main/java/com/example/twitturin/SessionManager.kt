package com.example.twitturin

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_TOKEN = "token"
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPrefs.edit()

    fun saveToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPrefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        editor.remove(KEY_TOKEN)
        editor.apply()
    }
}