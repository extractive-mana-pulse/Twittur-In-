package com.example.twitturin.feature.profile.data.di

import com.example.twitturin.feature.profile.data.ProfileRepositoryImpl
import com.example.twitturin.feature.profile.domain.ProfileRepository
import org.koin.dsl.module

val profileDataModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(httpClient = get()) }
}
