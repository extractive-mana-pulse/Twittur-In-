package com.example.twitturin.feature.auth.presentation.stayin

import androidx.lifecycle.ViewModel
import com.example.twitturin.feature.auth.domain.AuthRepository

class StayInViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    /** Persist the user's stay-logged-in choice (true on "Save", false on "Not now"). */
    fun setRemembered(remembered: Boolean) {
        authRepository.setRemembered(remembered)
    }
}
