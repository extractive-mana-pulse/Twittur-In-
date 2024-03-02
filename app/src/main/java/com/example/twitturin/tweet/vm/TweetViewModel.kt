package com.example.twitturin.tweet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.profile.sealed.EditTweetResult
import com.example.twitturin.tweet.model.network.TweetApi
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TweetViewModel @Inject constructor(private val tweetApi: TweetApi) : ViewModel() {

    private val _deleteTweetResult = SingleLiveEvent<DeleteResult>()
    val deleteTweetResult: LiveData<DeleteResult> = _deleteTweetResult

    fun deleteTweet(tweetId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = tweetApi.deleteTweet(tweetId, token)
                if (response.isSuccessful) {
                    _deleteTweetResult.value = DeleteResult.Success
                } else {
                    _deleteTweetResult.value = DeleteResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteTweetResult.value = DeleteResult.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _editTweetResult = MutableLiveData<EditTweetResult>()
    val editTweetResult: LiveData<EditTweetResult> = _editTweetResult

    fun editTweet(content: String, tweetId: String, token: String) {
        val request = TweetContent(content)
        val authRequest = tweetApi.editTweet(tweetContent = request, tweetId = tweetId, token = token)
        authRequest.enqueue(object : Callback<TweetContent> {
            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {
                if (response.isSuccessful) {
                    val editTweet = response.body()
                    if (editTweet != null) {
                        _editTweetResult.value = EditTweetResult.Success(request)
                    }
                } else {
                    _editTweetResult.value = EditTweetResult.Error(response.code())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _editTweetResult.value = EditTweetResult.Error(404)
            }
        })
    }
}