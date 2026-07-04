package com.example.twitturin.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.example.twitturin.core.domain.auth.SessionSource

class HomeViewModel(
    private val sessionSource: SessionSource,
) : ViewModel() {

    val userId: String? = sessionSource.getUserId()

    /** Clears the token, user id and the remember-me flag. */
    fun logout() {
        sessionSource.clear()
    }
}
