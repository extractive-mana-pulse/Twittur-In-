package com.example.twitturin.feature.tweet.presentation.profile

import com.example.twitturin.feature.tweet.presentation.TweetUi

/** The three profile content tabs (X-style). */
enum class ProfileTab(val title: String) {
    POSTS("Posts"),
    REPLIES("Replies"),
    LIKES("Likes"),
}

data class ProfileTweetsState(
    val selectedTab: ProfileTab = ProfileTab.POSTS,
    val posts: List<TweetUi> = emptyList(),
    val replies: List<TweetUi> = emptyList(),
    val likes: List<TweetUi> = emptyList(),
    val loadingPosts: Boolean = false,
    val loadingReplies: Boolean = false,
    val loadingLikes: Boolean = false,
) {
    val currentList: List<TweetUi>
        get() = when (selectedTab) {
            ProfileTab.POSTS -> posts
            ProfileTab.REPLIES -> replies
            ProfileTab.LIKES -> likes
        }

    val currentLoading: Boolean
        get() = when (selectedTab) {
            ProfileTab.POSTS -> loadingPosts
            ProfileTab.REPLIES -> loadingReplies
            ProfileTab.LIKES -> loadingLikes
        }
}
