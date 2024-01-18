package com.example.twitturin.ui.sealeds

sealed class DeleteResult {
    data object Success : DeleteResult()
    data class Error(val message: String) : DeleteResult()
}