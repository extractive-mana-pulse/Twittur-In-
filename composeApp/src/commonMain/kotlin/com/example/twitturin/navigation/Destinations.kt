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

@Serializable
data object ProfileRoute

@Serializable
data object EditProfileRoute

@Serializable
data object FeedRoute

@Serializable
data object ComposeTweetRoute
