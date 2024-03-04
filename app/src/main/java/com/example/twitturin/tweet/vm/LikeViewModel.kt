package com.example.twitturin.tweet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.tweet.model.data.LikeTweet
import com.example.twitturin.tweet.model.domain.repository.TweetRepository
import com.example.twitturin.tweet.sealed.PostLike
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val repository : TweetRepository
): ViewModel() {

    private val _likePostResult = SingleLiveEvent<PostLike>()
    val likePostResult: LiveData<PostLike> = _likePostResult

    fun likePost(count: String, userId: String, token: String) {
        val request = LikeTweet(count)
        repository.like(request, userId,"Bearer $token").enqueue(object : Callback<LikeTweet> {

            override fun onResponse(call: Call<LikeTweet>, response: Response<LikeTweet>) {
                if (response.isSuccessful) {
                    val likePostResponse= response.body()
                    _likePostResult.value = likePostResponse?.let { PostLike.Success(it) }
                } else {
                    _likePostResult.value = PostLike.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<LikeTweet>, t: Throwable) {
                _likePostResult.value = PostLike.Error(t.message.toString())
            }
        })
    }

    private val _likeDeleteResult = SingleLiveEvent<PostLike>()
    val likeDeleteResult: LiveData<PostLike> = _likeDeleteResult

    fun unLikePost(count: String, userId: String, token: String) {
        val request = LikeTweet(count)
        repository.unLike(request, userId,"Bearer $token").enqueue(object : Callback<LikeTweet> {

            override fun onResponse(call: Call<LikeTweet>, response: Response<LikeTweet>) {
                if (response.isSuccessful) {
                    val likePostResponse= response.body()
                    _likeDeleteResult.value = likePostResponse?.let { PostLike.Success(it) }
                } else {
                    _likeDeleteResult.value = PostLike.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<LikeTweet>, t: Throwable) {
                _likeDeleteResult.value = PostLike.Error(t.message.toString())
            }
        })
    }
}