package com.example.twitturin.auth.domain.model

import com.google.gson.annotations.SerializedName

data class AuthUser(
    @SerializedName("id"        ) var id        : String? = null,
    @SerializedName("token"     ) var token     : String? = null
)
