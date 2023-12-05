package com.example.twitturin.model.data.users

import com.google.gson.annotations.SerializedName


data class Replies (

  @SerializedName("content"     ) var content     : String?            = null,
  @SerializedName("likedBy"     ) var likedBy     : ArrayList<LikedBy> = arrayListOf(),
  @SerializedName("tweet"       ) var tweet       : String?            = null,
  @SerializedName("parentTweet" ) var parentTweet : String?            = null,
  @SerializedName("author"      ) var author      : Author?            = Author(),
  @SerializedName("createdAt"   ) var createdAt   : String?            = null,
  @SerializedName("updatedAt"   ) var updatedAt   : String?            = null,
  @SerializedName("likes"       ) var likes       : Int?               = null,
  @SerializedName("replies"     ) var replies     : ArrayList<Replies> = arrayListOf(),
  @SerializedName("id"          ) var id          : String?            = null

)