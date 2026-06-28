package com.example.twitturin.feature.tweet.data

import com.example.twitturin.feature.tweet.domain.Tweet
import com.example.twitturin.feature.tweet.domain.TweetAuthor
import com.example.twitturin.feature.tweet.domain.TweetLiker

fun TweetDto.toTweet(): Tweet = Tweet(
    id = id.orEmpty(),
    content = content.orEmpty(),
    author = author?.toTweetAuthor(),
    createdAt = createdAt,
    likes = likes ?: 0,
    replyCount = replyCount ?: 0,
    likedBy = likedBy ?: emptyList(),
    isEdited = isEdited ?: false,
)

fun TweetAuthorDto.toTweetAuthor(): TweetAuthor = TweetAuthor(
    id = id.orEmpty(),
    username = username,
    fullName = fullName,
    profilePicture = profilePicture,
)

fun TweetLikerDto.toTweetLiker(): TweetLiker = TweetLiker(
    id = id.orEmpty(),
    username = username,
    fullName = fullName,
    profilePicture = profilePicture,
    bio = bio,
)
