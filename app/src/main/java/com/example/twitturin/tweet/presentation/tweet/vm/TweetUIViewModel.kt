package com.example.twitturin.tweet.presentation.tweet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.tweet.presentation.tweet.sealed.TweetUIEvents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class TweetUIViewModel: ViewModel() {

    private val _channel = Channel<TweetUIEvents>(Channel.BUFFERED)
    val channel = _channel.receiveAsFlow()

    fun onItemPressed() { viewModelScope.launch { _channel.send(TweetUIEvents.OnItemPressed) } }

    fun onHeartPressed() { viewModelScope.launch { _channel.send(TweetUIEvents.OnHeartPressed) } }

    fun onReplyPressed() { viewModelScope.launch { _channel.send(TweetUIEvents.OnReplyPressed) } }

    fun onSharePressed() { viewModelScope.launch { _channel.send(TweetUIEvents.OnSharePressed) } }

    fun onMorePressed() { viewModelScope.launch { _channel.send(TweetUIEvents.OnMorePressed) } }

}