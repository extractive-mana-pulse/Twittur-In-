package com.example.twitturin.tweet.presentation.postTweet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.tweet.presentation.postTweet.sealed.PostTweetUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PostTweetUIViewModel: ViewModel() {

    private val postTweetEventChannel = Channel<PostTweetUI>(Channel.BUFFERED)
    val signInEvent = postTweetEventChannel.receiveAsFlow()

    fun onCancelPressed() {
        viewModelScope.launch {
            postTweetEventChannel.send(PostTweetUI.OnCancelPressed)
        }
    }

    fun onPublishPressed() {
        viewModelScope.launch {
            postTweetEventChannel.send(PostTweetUI.OnPublishPressed)
        }
    }

    fun onDialogReadPolicyPressed() {
        viewModelScope.launch {
            postTweetEventChannel.send(PostTweetUI.OnDialogReadPolicyPressed)
        }
    }
}