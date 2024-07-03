package com.example.twitturin.detail.domain.model

import com.google.gson.annotations.SerializedName

data class UserLikesAPost (

    @SerializedName("username"  ) var username  : String? = null,
    @SerializedName("fullName"  ) var fullName  : String? = null,
    @SerializedName("profilePicture" ) var profilePicture : String? = null,
    @SerializedName("bio"       ) var bio       : String? = null,
    @SerializedName("id"        ) var id        : String? = null,
    @SerializedName("token"     ) var token     : String? = null,
)