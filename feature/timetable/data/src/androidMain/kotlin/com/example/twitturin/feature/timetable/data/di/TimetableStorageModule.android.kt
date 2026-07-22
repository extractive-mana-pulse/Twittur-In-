package com.example.twitturin.feature.timetable.data.di

import android.content.Context
import com.example.twitturin.feature.timetable.data.storage.TimetableRawFileStore
import org.koin.core.module.Module
import org.koin.dsl.module

/** Resolves the app `Context` Koin registers via `androidContext(...)` in `InitKoin`'s
 *  platform config (see `androidApp`'s `MainActivity`). */
actual val timetableStorageModule: Module = module {
    single { TimetableRawFileStore(context = get<Context>()) }
}
