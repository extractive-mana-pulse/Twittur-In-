package com.example.twitturin.feature.settings.presentation

import androidx.lifecycle.ViewModel
import com.example.twitturin.core.domain.preferences.AppAccent
import com.example.twitturin.core.domain.preferences.AppLanguage
import com.example.twitturin.core.domain.preferences.AppPreferences
import com.example.twitturin.core.domain.preferences.ThemeMode

/** Exposes the observable theme/language/customization preferences and their setters. */
class SettingsViewModel(
    private val appPreferences: AppPreferences,
) : ViewModel() {

    val themeMode = appPreferences.themeMode
    val language = appPreferences.language
    val accent = appPreferences.accent
    val showBarLabels = appPreferences.showBarLabels
    val showFab = appPreferences.showFab
    val awayMode = appPreferences.awayMode

    fun setThemeMode(mode: ThemeMode) = appPreferences.setThemeMode(mode)
    fun setLanguage(language: AppLanguage) = appPreferences.setLanguage(language)
    fun setAccent(accent: AppAccent) = appPreferences.setAccent(accent)
    fun setShowBarLabels(show: Boolean) = appPreferences.setShowBarLabels(show)
    fun setShowFab(show: Boolean) = appPreferences.setShowFab(show)
    fun setAwayMode(away: Boolean) = appPreferences.setAwayMode(away)
}
