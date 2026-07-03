package com.example.twitturin.notification.presentation.vm

import android.text.format.Formatter.formatFileSize
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.notification.data.remote.repository.GithubRepository
import com.example.twitturin.notification.domain.model.MainGit
import com.example.twitturin.notification.presentation.sealed.NotificationUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    private val _isLoading =  MutableStateFlow(false)
    val isLoading = _isLoading
        .onStart {
            getLatestReleaseTest()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    private val _gitResponse1 = MutableStateFlow<Response<MainGit>?>(null)
    val gitData = _gitResponse1
        .map { response ->
            response?.body()?.let { mainGit ->
                MainGit(
                    url = mainGit.url,
                    assetsUrl = mainGit.assetsUrl,
                    name = mainGit.name,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun getLatestReleaseTest() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getLatestRelease()
                _gitResponse.value = response
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error fetching release: ${e.message}")
                // You might want to handle the error with another StateFlow
            } finally {
                _isLoading.value = false
            }
        }
    }
}