package com.example.twitturin.notification.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.notification.data.remote.repository.GithubRepository
import com.example.twitturin.notification.domain.model.MainGit
import com.example.twitturin.notification.presentation.sealed.NotificationUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: GithubRepository) : ViewModel() {

    private val uiEvent = Channel<NotificationUIEvent>(Channel.BUFFERED)
    val notificationEvent = uiEvent.receiveAsFlow()

    fun onDownloadPressed() { viewModelScope.launch { uiEvent.send(NotificationUIEvent.OnDownloadPressed) } }
    fun onItemPressed() { viewModelScope.launch { uiEvent.send(NotificationUIEvent.OnItemPressed) } }

    private val _gitResponse =  MutableStateFlow<Response<MainGit>?>(null)
    val gitResponse: StateFlow<Response<MainGit>?> = _gitResponse.asStateFlow()

    fun getLatestRelease() {
        viewModelScope.launch {
            val response = repository.getLatestRelease()
            _gitResponse.value = response
        }
    }
}