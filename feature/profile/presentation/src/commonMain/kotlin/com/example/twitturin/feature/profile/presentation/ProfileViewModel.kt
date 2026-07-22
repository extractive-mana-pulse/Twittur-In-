package com.example.twitturin.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.follow.domain.FollowRepository
import com.example.twitturin.feature.profile.domain.EditProfile
import com.example.twitturin.feature.profile.domain.ProfileRepository
import com.example.twitturin.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Backs the profile + edit-profile screens. [targetUserId] selects whose profile: `null` (or the
 * signed-in user's own id) shows "me" with the owner actions; any other id shows a visitor view
 * with a follow/unfollow button instead.
 */
class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val sessionSource: SessionSource,
    private val followRepository: FollowRepository,
    targetUserId: String? = null,
) : ViewModel() {

    private val myUserId: String? = sessionSource.getUserId()
    private val profileUserId: String? = targetUserId ?: myUserId
    private val isMe: Boolean = targetUserId == null || targetUserId == myUserId

    private val _state = MutableStateFlow(ProfileState(isMe = isMe))
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ProfileState(isMe = isMe),
        )

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    fun refresh() = loadProfile()

    fun logout() {
        sessionSource.clear()
        viewModelScope.launch { _events.send(ProfileEvent.LoggedOut) }
    }

    fun deleteAccount() {
        val id = myUserId ?: return notSignedIn()
        viewModelScope.launch {
            profileRepository.deleteAccount(id)
                .onSuccess {
                    sessionSource.clear()
                    _events.send(ProfileEvent.AccountDeleted)
                }
                .onFailure { error -> _events.send(ProfileEvent.ShowError(error.toUiText())) }
        }
    }

    /** Visitor view: optimistically flip the follow button (and follower count), then sync. */
    fun toggleFollow() {
        val id = profileUserId ?: return
        if (isMe) return
        val current = _state.value.isFollowing ?: return
        val now = !current
        val delta = if (now) 1 else -1
        _state.update { state ->
            state.copy(
                isFollowing = now,
                user = state.user?.copy(followersCount = (state.user.followersCount + delta).coerceAtLeast(0)),
            )
        }
        viewModelScope.launch {
            val result = if (now) followRepository.followUser(id) else followRepository.unfollowUser(id)
            result.onFailure { error ->
                _state.update { state ->
                    state.copy(
                        isFollowing = current,
                        user = state.user?.copy(followersCount = (state.user.followersCount - delta).coerceAtLeast(0)),
                    )
                }
                _events.send(ProfileEvent.ShowError(error.toUiText()))
            }
        }
    }

    /** Own profile: upload a new avatar (`POST users/{id}/profilePicture`), then re-fetch. */
    fun uploadPhoto(bytes: ByteArray, fileName: String) {
        val id = myUserId ?: return notSignedIn()
        if (_state.value.isUpdatingPhoto) return
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingPhoto = true) }
            profileRepository.uploadProfilePicture(id, bytes, fileName)
                .onSuccess {
                    _state.update { it.copy(isUpdatingPhoto = false) }
                    loadProfile()
                }
                .onFailure { error ->
                    _state.update { it.copy(isUpdatingPhoto = false) }
                    _events.send(ProfileEvent.ShowError(error.toUiText()))
                }
        }
    }

    /** Own profile: remove the avatar (`DELETE users/{id}/profilePicture`), then re-fetch. */
    fun removePhoto() {
        val id = myUserId ?: return notSignedIn()
        if (_state.value.isUpdatingPhoto) return
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingPhoto = true) }
            profileRepository.deleteProfilePicture(id)
                .onSuccess {
                    _state.update { it.copy(isUpdatingPhoto = false) }
                    loadProfile()
                }
                .onFailure { error ->
                    _state.update { it.copy(isUpdatingPhoto = false) }
                    _events.send(ProfileEvent.ShowError(error.toUiText()))
                }
        }
    }

    fun editProfile(fullName: String, username: String, email: String, bio: String, country: String, birthday: String) {
        val id = myUserId ?: return notSignedIn()
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
        val id = profileUserId ?: return notSignedIn()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            profileRepository.getUser(id)
                .onSuccess { user -> _state.update { it.copy(isLoading = false, user = user.toProfileUi()) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toUiText()))
                }
            if (!isMe) loadFollowState(id)
        }
    }

    /** Whether the signed-in user is among this profile's followers. */
    private suspend fun loadFollowState(profileId: String) {
        val me = myUserId ?: return
        followRepository.getFollowers(profileId)
            .onSuccess { followers ->
                _state.update { it.copy(isFollowing = followers.any { f -> f.id == me }) }
            }
            .onFailure { /* keep null: the follow button stays hidden */ }
    }

    private fun notSignedIn() {
        viewModelScope.launch {
            _events.send(ProfileEvent.ShowError(UiText.DynamicString("You're not signed in.")))
        }
    }
}
