package com.example.twitturin.feature.timetable.presentation.di

import com.example.twitturin.feature.timetable.presentation.TimetableViewModel
import com.example.twitturin.feature.timetable.presentation.picker.TimetableSubjectPickerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val timetablePresentationModule = module {
    viewModelOf(::TimetableViewModel)
    viewModelOf(::TimetableSubjectPickerViewModel)
}
