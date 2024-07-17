package com.example.twitturin.tweet.presentation.editTweet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.tweet.data.remote.repository.TweetRepository
import com.example.twitturin.tweet.domain.model.TweetContent
import com.example.twitturin.tweet.presentation.editTweet.sealed.EditTweet
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EditTweetViewModel @Inject constructor(private val repository: TweetRepository) : ViewModel() {

    private val _editTweetResult = MutableLiveData<EditTweet>()
    val editTweetResult: LiveData<EditTweet> = _editTweetResult

    fun editTweet(content: String, tweetId: String, token: String) {

        val request = TweetContent(content, token)
        val authRequest = repository.editTweet(request, tweetId, token)

        authRequest.enqueue(object : Callback<TweetContent> {

            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {
                if (response.isSuccessful) {
                    val editTweet = response.body()
                    if (editTweet != null) {
                        _editTweetResult.value = EditTweet.Success(request)
                    }
                } else {
                    _editTweetResult.value = EditTweet.Error(response.code())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _editTweetResult.value = EditTweet.Error(404)
            }
        })
    }
}