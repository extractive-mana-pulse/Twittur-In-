package com.example.twitturin.di

import com.example.twitturin.core.data.di.coreDataModule
import com.example.twitturin.feature.auth.data.di.authDataModule
import com.example.twitturin.feature.auth.presentation.di.authPresentationModule
import com.example.twitturin.feature.home.presentation.di.homePresentationModule
import com.example.twitturin.feature.profile.data.di.profileDataModule
import com.example.twitturin.feature.profile.presentation.di.profilePresentationModule
import com.example.twitturin.feature.notification.data.di.notificationDataModule
import com.example.twitturin.feature.notification.presentation.di.notificationPresentationModule
import com.example.twitturin.feature.search.data.di.searchDataModule
import com.example.twitturin.feature.search.presentation.di.searchPresentationModule
import com.example.twitturin.feature.tweet.data.di.tweetDataModule
import com.example.twitturin.feature.tweet.presentation.di.tweetPresentationModule
import com.example.twitturin.feature.follow.data.di.followDataModule
import com.example.twitturin.feature.follow.presentation.di.followPresentationModule
import com.example.twitturin.feature.settings.presentation.di.settingsPresentationModule
import com.example.twitturin.feature.timetable.data.di.timetableDataModule
import com.example.twitturin.feature.timetable.data.di.timetableStorageModule
import com.example.twitturin.feature.timetable.presentation.di.timetablePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/** Guards against double-start. `GlobalContext` is JVM-only in koin-core, so we track it
 *  ourselves to stay multiplatform (iOS/native). initKoin runs once on the main thread per
 *  process from each platform entry point, so a plain flag is sufficient. */
private var koinStarted = false

/**
 * Starts Koin with all wired modules. Idempotent — safe to call from each platform
 * entry point (Android Activity recreation, iOS VC creation, Desktop main).
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    if (koinStarted) return
    koinStarted = true
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            searchDataModule,
            searchPresentationModule,
            notificationDataModule,
            notificationPresentationModule,
            authDataModule,
            authPresentationModule,
            homePresentationModule,
            profileDataModule,
            profilePresentationModule,
            tweetDataModule,
            tweetPresentationModule,
            followDataModule,
            followPresentationModule,
            settingsPresentationModule,
            timetableStorageModule,
            timetableDataModule,
            timetablePresentationModule,
        )
    }
}
