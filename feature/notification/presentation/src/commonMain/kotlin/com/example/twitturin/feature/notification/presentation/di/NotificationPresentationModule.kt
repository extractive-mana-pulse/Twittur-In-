package com.example.twitturin.feature.notification.presentation.di

import com.example.twitturin.feature.notification.presentation.NotificationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationPresentationModule = module {
    viewModelOf(::NotificationViewModel)
}
