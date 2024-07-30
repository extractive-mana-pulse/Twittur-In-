package com.example.twitturin.profile.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.io.InputStream
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

    private val _getUserCredentials = MutableStateFlow<UserCredentials>(UserCredentials.Loading)
    val getUserCredentials = _getUserCredentials.asStateFlow()

    fun getUserCredentials(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getLoggedInUserData(userId)
                if (response.isSuccessful) {
                    response.body()?.let { _getUserCredentials.value = UserCredentials.Success(it) }
                } else {
                    _getUserCredentials.value = UserCredentials.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _getUserCredentials.value = UserCredentials.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _editUserResult = MutableStateFlow<EditUser>(EditUser.Loading)
    val editUserResult = _editUserResult.asStateFlow()

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
                    response.body()?.let { _editUserResult.value = EditUser.Success(it) }
                } else {
                    _editUserResult.value = EditUser.Error(response.code())
                }

            }

            override fun onFailure(call: Call<EditProfile>, t: Throwable) {
                _editUserResult.value = EditUser.Error(404)
            }
        })
    }

    /** Somehow this code works. do not touch if you are not sure what the hell are you want to do with this block of code */
    private val _editUserImageState = MutableStateFlow<EditUserImageState>(EditUserImageState.Loading)
    val editUserImageState = _editUserImageState.asStateFlow()

    fun editUserImage(picture: InputStream, userId: String, token: String) {

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), picture.readBytes())
        val imagePart = MultipartBody.Part.createFormData("picture", picture.toString(), requestBody)

        val authRequest = repository.loadImage(imagePart, userId, token)

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