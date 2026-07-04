package com.example.twitturin.feature.auth.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult

/** Contract for auth (sign-in / sign-up) + the "remember me" preference. Implemented in :feature:auth:data. */
interface AuthRepository {

    /** Logs in and, on success, persists the token + user id to the session. */
    suspend fun login(username: String, password: String): EmptyResult<DataError.Network>

    suspend fun registerStudent(student: StudentRegistration): EmptyResult<DataError.Network>

    suspend fun registerProfessor(professor: ProfessorRegistration): EmptyResult<DataError.Network>

    /** Persists the stay-logged-in choice made on the StayIn screen. */
    fun setRemembered(remembered: Boolean)
}
