package com.example.twitturin.profile.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.event.SingleLiveEvent
import com.example.twitturin.profile.data.remote.repository.ProfileRepository
import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.sealed.EditUserImageState
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository): ViewModel() {

    private val _deleteResult = MutableStateFlow<AccountDelete>(AccountDelete.Loading)
    val deleteResult = _deleteResult.asStateFlow()

    fun deleteUser(userId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUser(userId, token)
                if (response.isSuccessful) {
                    _deleteResult.value = AccountDelete.Success
                } else {
                    _deleteResult.value = AccountDelete.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteResult.value = AccountDelete.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _getUserCredentials = SingleLiveEvent<UserCredentials>()
    val getUserCredentials: LiveData<UserCredentials> = _getUserCredentials

    fun getUserCredentials(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getLoggedInUserData(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    _getUserCredentials.value = user?.let { UserCredentials.Success(it) }
                } else {
                    _getUserCredentials.value = UserCredentials.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _getUserCredentials.value = UserCredentials.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _editUserResult = MutableLiveData<EditUser>()
    val editUserResult: LiveData<EditUser> = _editUserResult

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
        val authRequest = repository.editUser(request, userId, token)
        authRequest.enqueue(object : Callback<EditProfile> {
            override fun onResponse(call: Call<EditProfile>, response: Response<EditProfile>) {
                if (response.isSuccessful) {
                    val editProfile = response.body()
                    if (editProfile != null) {
                        _editUserResult.value = EditUser.Success(editProfile)
                    }
                } else {
                    _editUserResult.value = EditUser.Error(response.code())
                }
            }

            override fun onFailure(call: Call<EditProfile>, t: Throwable) {
                _editUserResult.value = EditUser.Error(404)
            }
        })
    }

    private val _editUserImageState = MutableLiveData<EditUserImageState>()
    val editUserImageState: LiveData<EditUserImageState> = _editUserImageState

    fun editUserImage(userId: String, picture: String, token: String) {
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), picture)
        val imagePart = MultipartBody.Part.createFormData("picture", picture, requestBody)

        val authRequest = repository.loadImage(userId, imagePart, "Bearer $token")

        authRequest.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val editProfile = response.body()
                    _editUserImageState.value = EditUserImageState.Success(editProfile!!)
                } else {
                    _editUserImageState.value = EditUserImageState.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _editUserImageState.value = EditUserImageState.Error(t.message.toString())
            }
        })
    }
}