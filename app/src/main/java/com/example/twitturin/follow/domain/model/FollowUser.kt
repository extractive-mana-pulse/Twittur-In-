package com.example.twitturin.follow.domain.model

import com.google.gson.annotations.SerializedName

data class FollowUser (

  @SerializedName("bio"       ) var bio       : String? = null,
  @SerializedName("id"        ) var id        : String? = null,
  @SerializedName("token"     ) var token     : String? = null,
  @SerializedName("fullName"  ) var fullName  : String? = null,
  @SerializedName("username"  ) var username  : String? = null,
  @SerializedName("profilePicture" ) var profilePicture : String? = null,
)