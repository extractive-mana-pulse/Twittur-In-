package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.SingleLiveEvent
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.data.likeTweet.LikeTweet
import com.example.twitturin.ui.sealeds.PostLikeResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LikeViewModel: ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val likePostApi: Api = retrofit.create(Api::class.java)
    private val _likePostResult = SingleLiveEvent<PostLikeResult>()
    val likePostResult: LiveData<PostLikeResult> = _likePostResult

    fun likePost(count: String, token: String) {
        val request = LikeTweet(count)
        likePostApi.postLike(request,"Bearer $token").enqueue(object : Callback<LikeTweet> {

            override fun onResponse(call: Call<LikeTweet>, response: Response<LikeTweet>) {
                if (response.isSuccessful) {
                    val likePostResponse= response.body()
                    _likePostResult.value = likePostResponse?.let { PostLikeResult.Success(it) }
                } else {
                    _likePostResult.value = PostLikeResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<LikeTweet>, t: Throwable) {
                _likePostResult.value = PostLikeResult.Error(t.message.toString())
            }
        })
    }
}