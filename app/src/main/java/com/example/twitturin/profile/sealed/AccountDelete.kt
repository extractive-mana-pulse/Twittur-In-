package com.example.twitturin.profile.sealed

sealed class AccountDelete {
    data object Success : AccountDelete()
    data class Error(val message: String) : AccountDelete()
}