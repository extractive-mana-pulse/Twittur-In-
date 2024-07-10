package com.example.twitturin.profile.presentation.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.event.SingleLiveEvent
import com.example.twitturin.profile.data.remote.repository.ProfileRepository
import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.domain.model.ImageResource
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.sealed.EditUserImage
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {

    private val _deleteResult = SingleLiveEvent<AccountDelete>()
    val deleteResult: LiveData<AccountDelete> = _deleteResult

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

    private val _editUserImageState = MutableStateFlow<EditUserImageState>(EditUserImageState.Loading)
    val editUserImageState: StateFlow<EditUserImageState> = _editUserImageState.asStateFlow()

    fun editUserImage(image: String, userId: String, token: String) {
        viewModelScope.launch {
            try {
                val result = repository.loadImage(ImageResource(image), userId, token)
                _editUserImageState.value = EditUserImageState.Success(result.toString())
            } catch (e: Exception) {
                _editUserImageState.value = EditUserImageState.Error(e.message.orEmpty())
            }
        }
    }

    sealed class EditUserImageState {
        data object Loading : EditUserImageState()
        data class Success(val result: String) : EditUserImageState()
        data class Error(val message: String) : EditUserImageState()
    }
}
