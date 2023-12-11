package com.example.twitturin.model.data.users

import com.google.gson.annotations.SerializedName


data class User (

  @SerializedName("major"     ) var major     : String? = null,
  @SerializedName("studentId" ) var studentId : String? = null,
  @SerializedName("username"  ) var username  : String? = null,
  @SerializedName("fullName"  ) var fullName  : String? = null,
  @SerializedName("email"     ) var email     : String? = null,
  @SerializedName("age"       ) var age       : Int?    = null,
  @SerializedName("country"   ) var country   : String? = null,
  @SerializedName("kind"      ) var kind      : String? = null,
  @SerializedName("bio"       ) var bio       : String? = null,
  @SerializedName("birthday"  ) var birthday  : String? = null,
  @SerializedName("id"        ) var id        : String? = null,
  @SerializedName("token"     ) var token     : String? = null

)