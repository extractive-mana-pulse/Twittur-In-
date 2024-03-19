package com.example.twitturin.profile.data.data

import com.google.gson.annotations.SerializedName

data class EditProfile(

    @SerializedName("fullName"  ) val fullName  : String? = null,
    @SerializedName("username"  ) val username  : String? = null,
    @SerializedName("email"     ) val email     : String? = null,
    @SerializedName("bio"       ) val bio       : String? = null,
    @SerializedName("country"   ) val country   : String? = null,
    @SerializedName("birthday"  ) val birthday  : String? = null,

)
