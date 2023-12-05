package com.example.twitturin.model.data.tweets

import com.google.gson.annotations.SerializedName

data class Tweet (

    @SerializedName("content"    ) var content    : String?            = null,
    @SerializedName("author"     ) var author     : Author?            = Author(),
    @SerializedName("likedBy"    ) var likedBy    : ArrayList<LikedBy> = arrayListOf(),
    @SerializedName("createdAt"  ) val createdAt  : String,
    @SerializedName("updatedAt"  ) var updatedAt  : String?            = null,
    @SerializedName("likes"      ) var likes      : Int?               = null,
    @SerializedName("replies"    ) var replies    : ArrayList<Replies> = arrayListOf(),
    @SerializedName("replyCount" ) var replyCount : Int?               = null,
    @SerializedName("id"         ) var id         : String?            = null

)