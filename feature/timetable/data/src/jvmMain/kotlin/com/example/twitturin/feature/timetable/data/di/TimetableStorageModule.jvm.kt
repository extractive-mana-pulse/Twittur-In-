package com.example.twitturin.feature.timetable.data.di

import com.example.twitturin.feature.timetable.data.storage.TimetableRawFileStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val timetableStorageModule: Module = module {
    single { TimetableRawFileStore() }
}
