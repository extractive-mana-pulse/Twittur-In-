package com.example.twitturin.feature.timetable.data.di

import com.example.twitturin.feature.timetable.data.TimetableRepositoryImpl
import com.example.twitturin.feature.timetable.data.storage.TimetableLocalStore
import com.example.twitturin.feature.timetable.domain.TimetableRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Platform-agnostic bindings. [timetableStorageModule] (expect/actual) supplies the
 *  [com.example.twitturin.feature.timetable.data.storage.TimetableRawFileStore] this depends on. */
val timetableDataModule = module {
    singleOf(::TimetableLocalStore)
    singleOf(::TimetableRepositoryImpl) { bind<TimetableRepository>() }
}
