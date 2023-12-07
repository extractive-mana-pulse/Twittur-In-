package com.example.twitturin

import android.content.Context
import android.content.SharedPreferences

class SessionManagerUserId(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_USER_ID = "user_id"
    }

    fun saveUserId(userId: String) {
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    internal fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    var userId: String?
        get() = sharedPreferences.getString(KEY_USER_ID, null)
        set(value) {
            editor.putString(KEY_USER_ID, value)
            editor.apply()
        }
}