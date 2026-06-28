package com.example.twitturin.feature.search.presentation

import com.example.twitturin.core.presentation.UiText

sealed interface SearchEvent {
    data class NavigateToProfile(val userId: String) : SearchEvent
    data class ShowError(val message: UiText) : SearchEvent
}
