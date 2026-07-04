package com.example.twitturin.feature.auth.presentation.di

import com.example.twitturin.feature.auth.presentation.login.LoginViewModel
import com.example.twitturin.feature.auth.presentation.registration.RegistrationViewModel
import com.example.twitturin.feature.auth.presentation.stayin.StayInViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::StayInViewModel)
}
