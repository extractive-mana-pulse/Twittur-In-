package com.example.twitturin.feature.notification.data.di

import com.example.twitturin.feature.notification.data.NotificationRepositoryImpl
import com.example.twitturin.feature.notification.domain.NotificationRepository
import org.koin.dsl.module

val notificationDataModule = module {
    single<NotificationRepository> { NotificationRepositoryImpl(httpClient = get()) }
}
