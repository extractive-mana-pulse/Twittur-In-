package com.example.twitturin.auth.presentation.registration.vm

import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.data.remote.repository.AuthRepository
import com.example.twitturin.auth.domain.model.SignUpProf
import com.example.twitturin.auth.domain.model.SignUpStudent
import com.example.twitturin.auth.presentation.registration.professor.sealed.SignUpProfResult
import com.example.twitturin.auth.presentation.registration.student.sealed.SignUpStudentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository : AuthRepository) : ViewModel() {

    private val _profRegResult = MutableStateFlow<SignUpProfResult>(SignUpProfResult.Loading)
    val profRegResult = _profRegResult.asStateFlow()

    fun signUpProf(fullName: String, username: String, subject: String, password: String, kind: String) {

        val request = SignUpProf(fullName, username, subject, password, kind)
        repository.signUpProf(request).enqueue(object : Callback<SignUpProf> {
            override fun onResponse(call: Call<SignUpProf>, response: Response<SignUpProf>) {

                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _profRegResult.value = signUpResponse?.let { SignUpProfResult.Success(it) }!!
                } else {
                    _profRegResult.value = SignUpProfResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<SignUpProf>, t: Throwable) {
                _profRegResult.value = SignUpProfResult.Error(t.message.toString())
            }
        })
    }

    private val _signUpStudentResult = MutableStateFlow<SignUpStudentResult>(SignUpStudentResult.Loading)
    val signUpStudentResult = _signUpStudentResult.asStateFlow()

    fun signUpStudent(fullName: String, username: String, studentId: String, major: String, password: String, kind: String) {

        val request = SignUpStudent(fullName, username, studentId, major, password, kind)
        repository.signUpStudent(request).enqueue(object : Callback<SignUpStudent> {

            override fun onResponse(call: Call<SignUpStudent>, response: Response<SignUpStudent>) {

                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _signUpStudentResult.value = signUpResponse?.let { SignUpStudentResult.Success(it) }!!
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