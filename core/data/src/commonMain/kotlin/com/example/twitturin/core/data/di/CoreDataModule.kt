package com.example.twitturin.core.data.di

import com.example.twitturin.core.data.auth.SettingsSessionSource
import com.example.twitturin.core.data.network.HttpClientFactory
import com.example.twitturin.core.data.network.httpClientEngine
import com.example.twitturin.core.data.preferences.SettingsAppPreferences
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.preferences.AppPreferences
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDataModule = module {
    single<Settings> { Settings() }
    singleOf(::SettingsSessionSource) { bind<SessionSource>() }
    singleOf(::SettingsAppPreferences) { bind<AppPreferences>() }
    single<HttpClient> { HttpClientFactory.create(engine = httpClientEngine(), sessionSource = get()) }
}
