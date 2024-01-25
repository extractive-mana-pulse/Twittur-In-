package com.example.twitturin.model.data

import com.google.gson.annotations.SerializedName

data class ListOfFollowing(
    @SerializedName("f_username") var username  : String? = null,
)
