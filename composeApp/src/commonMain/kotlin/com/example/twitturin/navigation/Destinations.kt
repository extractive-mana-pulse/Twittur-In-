package com.example.twitturin.navigation

import kotlinx.serialization.Serializable

/** Type-safe Compose Navigation destinations. One per ported feature; grows during the migration. */
@Serializable
data object SearchRoute

@Serializable
data object NotificationRoute

@Serializable
data object PatchNoteRoute

@Serializable
data object SignInRoute

@Serializable
data object KindRoute

@Serializable
data object StudentRegistrationRoute

@Serializable
data object ProfessorRegistrationRoute

@Serializable
data object StayInRoute

@Serializable
data object HomeRoute

/** A user profile. [userId] = null (default) shows the signed-in user's own profile. */
@Serializable
data class ProfileRoute(val userId: String? = null)

@Serializable
data object EditProfileRoute

@Serializable
data object FeedRoute

/** Compose a new tweet, or edit an existing one when [tweetId] is set ([initialText] prefills it). */
@Serializable
data class ComposeTweetRoute(val tweetId: String? = null, val initialText: String? = null)

@Serializable
data class DetailRoute(val tweetId: String, val focusReply: Boolean = false)

@Serializable
data class LikesListRoute(val tweetId: String)

@Serializable
data class FollowersRoute(val userId: String)

@Serializable
data class FollowingRoute(val userId: String)

@Serializable
data object SettingsRoute

@Serializable
data object FeedbackRoute

@Serializable
data object TimetableSubjectPickerRoute
