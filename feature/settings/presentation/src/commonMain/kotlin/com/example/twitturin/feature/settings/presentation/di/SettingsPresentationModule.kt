package com.example.twitturin.feature.settings.presentation.di

import com.example.twitturin.feature.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsPresentationModule = module {
    viewModelOf(::SettingsViewModel)
}
