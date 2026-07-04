package com.example.twitturin.feature.follow.presentation.di

import com.example.twitturin.feature.follow.presentation.FollowViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val followPresentationModule = module {
    viewModelOf(::FollowViewModel)
}
