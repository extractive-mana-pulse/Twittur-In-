package com.example.twitturin.core.data.auth

import com.example.twitturin.core.domain.auth.SessionSource
import com.russhwolf.settings.Settings

/** [SessionSource] backed by Multiplatform Settings (SharedPreferences / NSUserDefaults / etc.). */
class SettingsSessionSource(private val settings: Settings) : SessionSource {

    override fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)

    override fun setToken(token: String?) {
        if (token == null) settings.remove(KEY_TOKEN) else settings.putString(KEY_TOKEN, token)
    }

    override fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)

    override fun setUserId(userId: String?) {
        if (userId == null) settings.remove(KEY_USER_ID) else settings.putString(KEY_USER_ID, userId)
    }

    override fun isRemembered(): Boolean = settings.getBoolean(KEY_REMEMBERED, false)

    override fun setRemembered(remembered: Boolean) {
        settings.putBoolean(KEY_REMEMBERED, remembered)
    }

    override fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_REMEMBERED)
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_REMEMBERED = "is_logged_in"
    }
}
