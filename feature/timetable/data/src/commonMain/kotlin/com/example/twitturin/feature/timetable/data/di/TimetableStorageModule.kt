package com.example.twitturin.feature.timetable.data.di

import org.koin.core.module.Module

/**
 * Constructs [com.example.twitturin.feature.timetable.data.storage.TimetableRawFileStore], whose
 * constructor deliberately isn't uniform across platforms (Android needs a `Context`, iOS/desktop
 * don't) — see that class's doc. Each platform wires its own here; [timetableDataModule] only
 * ever reaches it through Koin's `get()`, so it doesn't care which platform provided it.
 */
expect val timetableStorageModule: Module
