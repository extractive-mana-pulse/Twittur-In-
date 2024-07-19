package com.example.twitturin.core.manager

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "useId"
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

    fun getUserId(): String? {
        return sharedPrefs.getString(KEY_USER_ID, null)
    }

    fun saveUserID(userId: String) {
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }
}