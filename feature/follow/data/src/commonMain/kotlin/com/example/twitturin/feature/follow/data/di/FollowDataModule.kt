package com.example.twitturin.feature.follow.data.di

import com.example.twitturin.feature.follow.data.FollowRepositoryImpl
import com.example.twitturin.feature.follow.domain.FollowRepository
import org.koin.dsl.module

val followDataModule = module {
    single<FollowRepository> { FollowRepositoryImpl(httpClient = get()) }
}
