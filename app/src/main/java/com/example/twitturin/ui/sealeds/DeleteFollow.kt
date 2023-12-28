package com.example.twitturin.ui.sealeds

sealed class DeleteFollow {
    data object Success : DeleteFollow()
    data class Error(val message: String) : DeleteFollow()
}