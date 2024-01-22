package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.editUser.EditProfile

sealed class EditUserResult {

    data class Success(val editUser : EditProfile) : EditUserResult()
    data class Error(val statusCode: Int) : EditUserResult() {
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