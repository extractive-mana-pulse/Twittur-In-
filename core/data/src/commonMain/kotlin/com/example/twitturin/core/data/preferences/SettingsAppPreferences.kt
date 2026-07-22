package com.example.twitturin.core.data.preferences

import com.example.twitturin.core.domain.preferences.AppAccent
import com.example.twitturin.core.domain.preferences.AppLanguage
import com.example.twitturin.core.domain.preferences.AppPreferences
import com.example.twitturin.core.domain.preferences.ThemeMode
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** [AppPreferences] backed by Multiplatform Settings; exposes observable StateFlows. */
class SettingsAppPreferences(private val settings: Settings) : AppPreferences {

    private val _themeMode = MutableStateFlow(readTheme())
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.fromCode(settings.getStringOrNull(KEY_LANG)))
    override val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _accent = MutableStateFlow(AppAccent.fromName(settings.getStringOrNull(KEY_ACCENT)))
    override val accent: StateFlow<AppAccent> = _accent.asStateFlow()

    private val _showBarLabels = MutableStateFlow(settings.getBoolean(KEY_BAR_LABELS, false))
    override val showBarLabels: StateFlow<Boolean> = _showBarLabels.asStateFlow()

    private val _expandedFab = MutableStateFlow(settings.getBoolean(KEY_EXPANDED_FAB, false))
    override val expandedFab: StateFlow<Boolean> = _expandedFab.asStateFlow()

    private val _awayMonths = MutableStateFlow(settings.getInt(KEY_AWAY_MONTHS, 0))
    override val awayMonths: StateFlow<Int> = _awayMonths.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        settings.putString(KEY_THEME, mode.name)
        _themeMode.value = mode
    }

    override fun setLanguage(language: AppLanguage) {
        settings.putString(KEY_LANG, language.code)
        _language.value = language
    }

    override fun setAccent(accent: AppAccent) {
        settings.putString(KEY_ACCENT, accent.name)
        _accent.value = accent
    }

    override fun setShowBarLabels(show: Boolean) {
        settings.putBoolean(KEY_BAR_LABELS, show)
        _showBarLabels.value = show
    }

    override fun setExpandedFab(expanded: Boolean) {
        settings.putBoolean(KEY_EXPANDED_FAB, expanded)
        _expandedFab.value = expanded
    }

    override fun setAwayMonths(months: Int) {
        settings.putInt(KEY_AWAY_MONTHS, months)
        _awayMonths.value = months
    }

    private fun readTheme(): ThemeMode =
        settings.getStringOrNull(KEY_THEME)
            ?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
            ?: ThemeMode.SYSTEM

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_LANG = "app_language"
        const val KEY_ACCENT = "app_accent"
        const val KEY_BAR_LABELS = "show_bar_labels"
        const val KEY_EXPANDED_FAB = "expanded_fab"
        const val KEY_AWAY_MONTHS = "away_months"
    }
}
