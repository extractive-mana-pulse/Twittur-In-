package com.example.twitturin.feature.tweet.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.feature.tweet.domain.TweetRepository
import com.example.twitturin.feature.tweet.presentation.TweetUi
import com.example.twitturin.feature.tweet.presentation.toTweetUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Backs the profile's Posts / Replies / Likes tabs. Loads all three lists for [load]'s userId and
 * supports the heart toggle inside any of them. Navigation is handled by the host composable's
 * callbacks; failures fall back to the tab's empty state.
 */
class ProfileTweetsViewModel(
    private val tweetRepository: TweetRepository,
    sessionSource: SessionSource,
) : ViewModel() {

    private val currentUserId: String? = sessionSource.getUserId()
    private var loadedUserId: String? = null

    private val _state = MutableStateFlow(ProfileTweetsState())
    val state = _state.asStateFlow()

    fun load(userId: String) {
        if (loadedUserId == userId) return
        loadedUserId = userId
        loadPosts(userId)
        loadReplies(userId)
        loadLikes(userId)
    }

    fun selectTab(tab: ProfileTab) = _state.update { it.copy(selectedTab = tab) }

    fun refresh() {
        loadedUserId?.let { id ->
            loadPosts(id)
            loadReplies(id)
            loadLikes(id)
        }
    }

    private fun loadPosts(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loadingPosts = true) }
            tweetRepository.getUserTweets(userId).onSuccess { list ->
                _state.update { it.copy(posts = list.map { t -> t.toTweetUi(currentUserId) }) }
            }
            _state.update { it.copy(loadingPosts = false) }
        }
    }

    private fun loadReplies(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loadingReplies = true) }
            tweetRepository.getUserReplies(userId).onSuccess { list ->
                _state.update { it.copy(replies = list.map { t -> t.toTweetUi(currentUserId) }) }
            }
            _state.update { it.copy(loadingReplies = false) }
        }
    }

    private fun loadLikes(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loadingLikes = true) }
            tweetRepository.getUserLikes(userId).onSuccess { list ->
                _state.update { it.copy(likes = list.map { t -> t.toTweetUi(currentUserId) }) }
            }
            _state.update { it.copy(loadingLikes = false) }
        }
    }

    /** Optimistically toggle the heart wherever the tweet appears, then sync; revert on failure. */
    fun toggleLike(tweetId: String) {
        val current: TweetUi = listOf(_state.value.posts, _state.value.replies, _state.value.likes)
            .firstNotNullOfOrNull { list -> list.firstOrNull { it.id == tweetId } } ?: return
        val nowLiked = !current.isLiked
        val newCount = (current.likes + if (nowLiked) 1 else -1).coerceAtLeast(0)
        _state.update { applyLike(it, tweetId, nowLiked, newCount) }
        viewModelScope.launch {
            val result = if (nowLiked) {
                tweetRepository.likeTweet(tweetId, newCount)
            } else {
                tweetRepository.unlikeTweet(tweetId, newCount)
            }
            result.onFailure {
                _state.update { applyLike(it, tweetId, current.isLiked, current.likes) }
            }
        }
    }

    private fun applyLike(state: ProfileTweetsState, tweetId: String, liked: Boolean, count: Int): ProfileTweetsState {
        fun List<TweetUi>.patch() = map { if (it.id == tweetId) it.copy(isLiked = liked, likes = count) else it }
        return state.copy(posts = state.posts.patch(), replies = state.replies.patch(), likes = state.likes.patch())
    }
}
