package com.example.twitturin.auth.domain.repository

import com.example.twitturin.auth.presentation.model.data.Login
import com.example.twitturin.auth.presentation.model.data.SignUpProf
import com.example.twitturin.auth.presentation.model.data.SignUpStudent
import com.example.twitturin.auth.presentation.model.data.User
import retrofit2.Call

interface AuthRepository {

    fun signInUser(authUser : Login) : Call<User>

    fun signUpProf(user : SignUpProf) : Call<SignUpProf>

    fun signUpStudent(user : SignUpStudent) : Call<SignUpStudent>

}