package com.example.twitturin.feature.tweet.presentation.likes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.tweet.domain.TweetRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LikesListViewModel(
    private val tweetRepository: TweetRepository,
) : ViewModel() {

    private var tweetId: String? = null

    private val _state = MutableStateFlow(LikesListState())
    val state = _state.asStateFlow()

    private val _errors = Channel<UiText>()
    val errors = _errors.receiveAsFlow()

    /** Called once from the screen with the tweet id from the nav route. */
    fun load(id: String) {
        if (tweetId == id && _state.value.likers.isNotEmpty()) return
        tweetId = id
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            tweetRepository.getLikers(id)
                .onSuccess { likers ->
                    _state.update { it.copy(isLoading = false, likers = likers.map { l -> l.toLikerUi() }) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _errors.send(error.toUiText())
                }
        }
    }
}
