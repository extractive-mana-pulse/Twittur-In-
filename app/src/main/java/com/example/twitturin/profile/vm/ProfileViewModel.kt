package com.example.twitturin.profile.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.network.Api
import com.example.twitturin.profile.model.network.ProfileApi
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.profile.sealed.EditTweetResult
import com.example.twitturin.profile.sealed.EditUserResult
import com.example.twitturin.profile.sealed.UserCredentialsResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileApi: ProfileApi): ViewModel() {


    /** this code is all about delete user's account. */

    private val _deleteResult = SingleLiveEvent<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    fun deleteUser(userId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = profileApi.deleteUser(userId, token)
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

    private val _getUserCredentials = MutableLiveData<UserCredentialsResult>()
    val getUserCredentials: LiveData<UserCredentialsResult> = _getUserCredentials

    fun getUserCredentials(userId: String) {
        viewModelScope.launch {
            try {
                val response = profileApi.getLoggedInUserData(userId)
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
        val authRequest = profileApi.editUser(request, userId, token)
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
}
