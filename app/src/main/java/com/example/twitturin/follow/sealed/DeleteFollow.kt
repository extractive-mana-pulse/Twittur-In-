package com.example.twitturin.follow.sealed

sealed class DeleteFollow {
    data object Success : DeleteFollow()
    data class Error(val message: String) : DeleteFollow()
}