package com.example.twitturin.tweet.domain.model

import android.os.Parcelable
import com.example.twitturin.profile.domain.model.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tweet (

    @SerializedName("content"    ) val content    : String? = null,
    @SerializedName("author"     ) val author     : User? = null,
    @SerializedName("createdAt"  ) val createdAt  : String? = null,
    @SerializedName("updatedAt"  ) val updatedAt  : String? = null,
    @SerializedName("likes"      ) var likes      : Int? = null,
    @SerializedName("likesBy"    ) val likedBy    : List<String>? = null,
    @SerializedName("replyCount" ) val replyCount : Int? = null,
    @SerializedName("id"         ) val id         : String? = null

) : Parcelable