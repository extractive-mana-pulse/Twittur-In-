package com.example.twitturin.feature.search.data.di

import com.example.twitturin.feature.search.data.SearchRepositoryImpl
import com.example.twitturin.feature.search.domain.SearchRepository
import org.koin.dsl.module

val searchDataModule = module {
    single<SearchRepository> { SearchRepositoryImpl(httpClient = get()) }
}
