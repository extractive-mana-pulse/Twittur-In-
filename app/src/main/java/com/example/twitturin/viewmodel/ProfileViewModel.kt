package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.network.Api
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.EditTweetResult
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val api: Api): ViewModel() {

    private val _deleteResult = SingleLiveEvent<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    fun deleteUser(userId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = api.deleteUser(userId, token)
                if (response.isSuccessful) {
                    _deleteResult.value = DeleteResult.Success
                } else {
                    _deleteResult.value = DeleteResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteResult.value = DeleteResult.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _deleteTweetResult = SingleLiveEvent<DeleteResult>()
    val deleteTweetResult: LiveData<DeleteResult> = _deleteTweetResult

    fun deleteTweet(tweetId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = api.deleteTweet(tweetId, token)
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


    private val _getUserCredentials = MutableLiveData<UserCredentialsResult>()
    val getUserCredentials: LiveData<UserCredentialsResult> = _getUserCredentials

    fun getUserCredentials(userId: String) {
        viewModelScope.launch {
            try {
                val response = api.getLoggedInUserData(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    _getUserCredentials.value = user?.let { UserCredentialsResult.Success(it) }
                } else {
                    _getUserCredentials.value = UserCredentialsResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _getUserCredentials.value = UserCredentialsResult.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _editUserResult = MutableLiveData<EditUserResult>()
    val editUserResult: LiveData<EditUserResult> = _editUserResult

    fun editUser(
        fullName: String,
        username: String,
        email: String,
        bio: String,
        country: String,
        birthday: String,
        userId: String,
        token: String
    ) {
        val request =  EditProfile(fullName, username, email, bio, country, birthday)
        /*username, email, birthday, bio, country, fullName*/
        val authRequest = api.editUser(request, userId, token)
        authRequest.enqueue(object : Callback<EditProfile> {
            override fun onResponse(call: Call<EditProfile>, response: Response<EditProfile>) {
                if (response.isSuccessful) {
                    val editProfile = response.body()
                    if (editProfile != null) {
                        _editUserResult.value = EditUserResult.Success(editProfile)
                    }
                } else {
                    _editUserResult.value = EditUserResult.Error(response.code())
                }
            }

            override fun onFailure(call: Call<EditProfile>, t: Throwable) {
                _editUserResult.value = EditUserResult.Error(404)
            }
        })
    }

    private val _editTweetResult = MutableLiveData<EditTweetResult>()
    val editTweetResult: LiveData<EditTweetResult> = _editTweetResult

    fun editTweet(content: String, tweetId: String, token: String) {
        val request = TweetContent(content)
        val authRequest = api.editTweet(tweetContent = request, tweetId = tweetId, token = token)
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
