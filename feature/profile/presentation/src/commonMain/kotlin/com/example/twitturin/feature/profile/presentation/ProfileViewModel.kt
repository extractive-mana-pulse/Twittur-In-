package com.example.twitturin.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.profile.domain.EditProfile
import com.example.twitturin.feature.profile.domain.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared by the profile + edit-profile screens. Operates on the currently signed-in user. */
class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val sessionSource: SessionSource,
) : ViewModel() {

    private val userId: String? = sessionSource.getUserId()

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ProfileState(),
        )

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    fun refresh() = loadProfile()

    fun logout() {
        sessionSource.clear()
        viewModelScope.launch { _events.send(ProfileEvent.LoggedOut) }
    }

    fun deleteAccount() {
        val id = userId ?: return notSignedIn()
        viewModelScope.launch {
            profileRepository.deleteAccount(id)
                .onSuccess {
                    sessionSource.clear()
                    _events.send(ProfileEvent.AccountDeleted)
                }
                .onFailure { error -> _events.send(ProfileEvent.ShowError(error.toUiText())) }
        }
    }

    fun editProfile(fullName: String, username: String, email: String, bio: String, country: String, birthday: String) {
        val id = userId ?: return notSignedIn()
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            profileRepository.editProfile(
                userId = id,
                edit = EditProfile(
                    fullName = fullName.trim(),
                    username = username.trim(),
                    email = email.trim(),
                    bio = bio.trim(),
                    country = country.trim(),
                    birthday = birthday.trim(),
                ),
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.Saved)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun loadProfile() {
        val id = userId ?: return notSignedIn()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            profileRepository.getUser(id)
                .onSuccess { user -> _state.update { it.copy(isLoading = false, user = user.toProfileUi()) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun notSignedIn() {
        viewModelScope.launch {
            _events.send(ProfileEvent.ShowError(UiText.DynamicString("You're not signed in.")))
        }
    }
}
