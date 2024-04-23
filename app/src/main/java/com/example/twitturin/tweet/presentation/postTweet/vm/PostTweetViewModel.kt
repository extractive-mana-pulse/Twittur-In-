package com.example.twitturin.tweet.presentation.postTweet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.event.SingleLiveEvent
import com.example.twitturin.tweet.data.remote.repository.TweetRepository
import com.example.twitturin.tweet.domain.model.TweetContent
import com.example.twitturin.tweet.presentation.postTweet.sealed.PostTweet
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PostTweetViewModel @Inject constructor(private val repository: TweetRepository): ViewModel() {

    private val _postTweet = SingleLiveEvent<PostTweet>()
    val postTweetResult: LiveData<PostTweet> = _postTweet

    fun postTheTweet(content: String, authToken: String) {
        val request = TweetContent(content,"")
        val authRequest = repository.postTweet(request, authToken)

        authRequest.enqueue(object : Callback<TweetContent> {
            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {

                if (response.isSuccessful) {
                    val postTweet = response.body()
                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
                } else {
                    _postTweet.value = PostTweet.Error(response.body().toString())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _postTweet.value = PostTweet.Error("Network error")
            }
        })
    }
}