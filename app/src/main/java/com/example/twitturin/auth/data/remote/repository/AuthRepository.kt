package com.example.twitturin.auth.data.remote.repository

import com.example.twitturin.auth.domain.model.AuthUser
import com.example.twitturin.auth.domain.model.Login
import com.example.twitturin.auth.domain.model.SignUpProf
import com.example.twitturin.auth.domain.model.SignUpStudent
import retrofit2.Call

interface AuthRepository {

    fun signInUser(authUser : Login) : Call<AuthUser>

    fun signUpProf(user : SignUpProf) : Call<SignUpProf>

    fun signUpStudent(user : SignUpStudent) : Call<SignUpStudent>

}