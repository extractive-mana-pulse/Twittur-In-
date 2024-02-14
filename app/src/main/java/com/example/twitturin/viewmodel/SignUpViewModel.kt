package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.BuildConfig
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.data.registration.SignUpProf
import com.example.twitturin.model.data.registration.SignUpStudent
import com.example.twitturin.ui.sealeds.SignUpProfResult
import com.example.twitturin.ui.sealeds.SignUpStudentResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val api : Api) : ViewModel() {

    private val _profRegResult = SingleLiveEvent<SignUpProfResult>()
    val profRegResult: LiveData<SignUpProfResult> = _profRegResult

    fun signUpProf(fullName: String, username: String, subject: String, password: String, kind: String) {
        val request = SignUpProf(fullName, username, subject, password, kind)

        api.signUpProf(request).enqueue(object : Callback<SignUpProf> {
            override fun onResponse(call: Call<SignUpProf>, response: Response<SignUpProf>) {

                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _profRegResult.value = signUpResponse?.let { SignUpProfResult.Success(it) }
                } else {
                    _profRegResult.value = SignUpProfResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<SignUpProf>, t: Throwable) {
                _profRegResult.value = SignUpProfResult.Error(t.message.toString())
            }
        })
    }

    private val _signUpStudentResult = SingleLiveEvent<SignUpStudentResult>()
    val signUpStudentResult: LiveData<SignUpStudentResult> = _signUpStudentResult

    fun signUp(fullName: String, username: String, studentId: String, major: String, password: String, kind: String) {
        val request = SignUpStudent(fullName, username, studentId, major, password, kind)
        api.signUpStudent(request).enqueue(object : Callback<SignUpStudent> {
            override fun onResponse(call: Call<SignUpStudent>, response: Response<SignUpStudent>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _signUpStudentResult.value = signUpResponse?.let { SignUpStudentResult.Success(it) }
                } else {
                    _signUpStudentResult.value = SignUpStudentResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<SignUpStudent>, t: Throwable) {
                _signUpStudentResult.value = SignUpStudentResult.Error(t.message.toString())
            }
        })
    }
}