package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.editUser.EditProfile

sealed class EditUserResult {
    data class Success(val user: EditProfile) : EditUserResult()
    data class Error(val errorMessage: String) : EditUserResult()
}