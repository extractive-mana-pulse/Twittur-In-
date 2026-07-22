package com.example.twitturin.feature.profile.presentation.di

import com.example.twitturin.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profilePresentationModule = module {
    // Optional runtime parameter: the profile's user id (null/absent = the signed-in user).
    viewModel { params ->
        ProfileViewModel(
            profileRepository = get(),
            sessionSource = get(),
            followRepository = get(),
            targetUserId = params.getOrNull(),
        )
    }
}
