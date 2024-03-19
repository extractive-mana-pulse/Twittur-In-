package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.profile.data.data.EditProfile

sealed class EditUser {

    data class Success(val editUser : EditProfile) : EditUser()
    data class Error(val statusCode: Int) : EditUser() {
        val error: String
            get() = when (statusCode) {
                400 -> "Bad Request"
                401 -> "Unauthorized"
                403 -> "Forbidden"
                404 -> "Not Found"
                500 -> "Internal Server Error"
                else -> "Unknown Error"
            }
    }
}