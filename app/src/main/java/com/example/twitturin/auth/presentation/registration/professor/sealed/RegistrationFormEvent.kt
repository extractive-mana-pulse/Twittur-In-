package com.example.twitturin.auth.presentation.registration.professor.sealed

sealed class RegistrationFormEvent {
    data class EmailChanged(val email: String) : RegistrationFormEvent()
//    data class PasswordChanged(val password: String) : RegistrationFormEvent()
//    data class RepeatedPasswordChanged(
//        val repeatedPassword: String
//    ) : RegistrationFormEvent()
//
//    data class AcceptTerms(val isAccepted: Boolean) : RegistrationFormEvent()

    data object Submit: RegistrationFormEvent()
}