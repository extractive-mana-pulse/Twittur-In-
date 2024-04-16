package com.example.twitturin.auth.presentation.stayIn.sealed

sealed class StayIn {

    data object Success : StayIn()
    data class Error(val message: String) : StayIn()

}