package com.example.twitturin.model.data.users

import com.google.gson.annotations.SerializedName


data class Author (

  @SerializedName("major"     ) var major     : String? = null,
  @SerializedName("studentId" ) var studentId : String? = null,
  @SerializedName("username"  ) var username  : String? = null,
  @SerializedName("fullName"  ) var fullName  : String? = null,
  @SerializedName("email"     ) var email     : String? = null,
  @SerializedName("age"       ) var age       : Int?    = null,
  @SerializedName("country"   ) var country   : String? = null,
  @SerializedName("kind"      ) var kind      : String? = null,
  @SerializedName("id"        ) var id        : String? = null

)