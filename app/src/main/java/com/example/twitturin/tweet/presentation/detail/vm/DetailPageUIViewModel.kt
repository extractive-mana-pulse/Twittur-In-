package com.example.twitturin.tweet.presentation.detail.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.tweet.presentation.detail.sealed.DetailPageUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailPageUIViewModel: ViewModel() {

    private val uiEvent = Channel<DetailPageUI>(Channel.BUFFERED)
    val detailPageEvent = uiEvent.receiveAsFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnBackPressed)
        }
    }

    fun onFollowPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnFollowPressed)
        }
    }

    fun onMorePressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnMorePressed)
        }
    }

    fun onListOfLikesPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnListOfLikesPressed)
        }
    }

    fun onCommentsPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnCommentPressed)
        }
    }

    fun onLikePressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnLikePressed)
        }
    }

    fun onSharePressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnSharePressed)
        }
    }

    fun onSendReplyPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnSendReplyPressed)
        }
    }

    fun onAvatarLongPressed() {
        viewModelScope.launch {
            uiEvent.send(DetailPageUI.OnImagePressed)
        }
    }
}