package com.example.twitturin.feature.auth.data.di

import com.example.twitturin.feature.auth.data.AuthRepositoryImpl
import com.example.twitturin.feature.auth.domain.AuthRepository
import org.koin.dsl.module

val authDataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(httpClient = get(), sessionSource = get()) }
}
