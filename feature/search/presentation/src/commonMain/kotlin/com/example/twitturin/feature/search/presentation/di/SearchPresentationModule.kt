package com.example.twitturin.feature.search.presentation.di

import com.example.twitturin.feature.search.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchPresentationModule = module {
    viewModelOf(::SearchViewModel)
}
