package com.example.twitturin.auth.model.domain.repository

import com.example.twitturin.auth.model.data.Login
import com.example.twitturin.auth.model.data.SignUpProf
import com.example.twitturin.auth.model.data.SignUpStudent
import com.example.twitturin.auth.model.data.User
import retrofit2.Call

interface AuthRepository {

    fun signInUser(authUser : Login) : Call<User>

    fun signUpProf(user : SignUpProf) : Call<SignUpProf>

    fun signUpStudent(user : SignUpStudent) : Call<SignUpStudent>

}