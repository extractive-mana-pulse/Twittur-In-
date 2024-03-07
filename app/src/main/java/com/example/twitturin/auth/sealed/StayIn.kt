package com.example.twitturin.auth.sealed

sealed class StayIn {

    data object Success : StayIn()
    data class Error(val message: String) : StayIn()

}