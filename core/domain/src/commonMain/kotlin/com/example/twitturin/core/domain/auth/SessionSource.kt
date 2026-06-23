package com.example.twitturin.core.domain.auth

/**
 * Single source of the current user's auth token + id. Backed by platform storage in
 * `core:data`; read by the Ktor bearer-auth plugin. Replaces the legacy SessionManager
 * (plain SharedPreferences read ad-hoc across ~13 fragments).
 */
interface SessionSource {
    fun getToken(): String?
    fun setToken(token: String?)
    fun getUserId(): String?
    fun setUserId(userId: String?)
    fun clear()
}
