package com.example.twitturin.feature.follow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.follow.domain.FollowRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Backs both the followers and following lists. Each nav entry (FollowersRoute / FollowingRoute)
 * gets its own Koin instance; [load] sets the target user + [FollowListMode] once on open.
 */
class FollowViewModel(
    private val followRepository: FollowRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FollowState())
    val state = _state.asStateFlow()

    private val _events = Channel<FollowEvent>()
    val events = _events.receiveAsFlow()

    private var targetUserId: String? = null

    fun load(userId: String, mode: FollowListMode) {
        targetUserId = userId
        _state.update { it.copy(mode = mode) }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = when (mode) {
                FollowListMode.FOLLOWERS -> followRepository.getFollowers(userId)
                FollowListMode.FOLLOWING -> followRepository.getFollowing(userId)
            }
            result
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasLoaded = true,
                            users = users.map { user -> user.toFollowUserUi() },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, hasLoaded = true) }
                    _events.send(FollowEvent.ShowMessage(error.toUiText()))
                }
        }
    }

    fun onAction(action: FollowAction) {
        when (action) {
            is FollowAction.OnUserClick -> viewModelScope.launch {
                _events.send(FollowEvent.NavigateToProfile(action.userId))
            }

            is FollowAction.OnActionClick -> toggleFollow(action.userId)
        }
    }

    private fun toggleFollow(userId: String) {
        val mode = _state.value.mode
        viewModelScope.launch {
            val result = when (mode) {
                FollowListMode.FOLLOWERS -> followRepository.followUser(userId)
                FollowListMode.FOLLOWING -> followRepository.unfollowUser(userId)
            }
            result
                .onSuccess {
                    val message = when (mode) {
                        FollowListMode.FOLLOWERS -> "Followed"
                        FollowListMode.FOLLOWING -> "Unfollowed"
                    }
                    _events.send(FollowEvent.ShowMessage(UiText.DynamicString(message)))
                }
                .onFailure { error -> _events.send(FollowEvent.ShowMessage(error.toUiText())) }
        }
    }
}
