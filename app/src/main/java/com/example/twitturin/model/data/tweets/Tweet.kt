package com.example.twitturin.model.data.tweets

import com.example.twitturin.model.data.users.User
import com.google.gson.annotations.SerializedName

data class Tweet (

    @SerializedName("content"    ) val content    : String,
    @SerializedName("author"     ) val author     : User? = User(),
    @SerializedName("createdAt"  ) val createdAt  : String,
    @SerializedName("updatedAt"  ) val updatedAt  : String,
    @SerializedName("likes"      ) var likes      : Int,
    @SerializedName("likesBy"    ) val likedBy    : List<String>,
    @SerializedName("replyCount" ) val replyCount : Int,
    @SerializedName("id"         ) val id         : String

)