package com.example.twitturin.core.domain.preferences

import kotlinx.coroutines.flow.StateFlow

/** App appearance mode. SYSTEM follows the OS setting. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Supported UI languages (display name shown in the picker). */
enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский"),
    UZBEK("uz", "Oʻzbekcha");

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

/**
 * Selectable app accent colour (drives the theme primary, bottom-bar highlight and FAB).
 * Stored as a platform-agnostic ARGB [argb] so :core:domain stays free of any UI dependency;
 * the design system maps it to a Compose `Color`.
 */
enum class AppAccent(val argb: Long, val displayName: String) {
    BLUE(0xFF1574A6, "Ocean"),
    PURPLE(0xFF7E3FF2, "Violet"),
    GREEN(0xFF2E9E5B, "Forest"),
    ORANGE(0xFFF4711E, "Sunset"),
    PINK(0xFFE0357B, "Blossom");

    companion object {
        fun fromName(name: String?): AppAccent = entries.firstOrNull { it.name == name } ?: BLUE
    }
}

/**
 * User-facing app preferences (theme, language and the Settings customizations), persisted across
 * launches and observable so the UI reacts immediately. Implemented in :core:data over
 * Multiplatform Settings.
 */
interface AppPreferences {
    val themeMode: StateFlow<ThemeMode>
    fun setThemeMode(mode: ThemeMode)

    val language: StateFlow<AppLanguage>
    fun setLanguage(language: AppLanguage)

    /** App accent colour (customizable from Settings). */
    val accent: StateFlow<AppAccent>
    fun setAccent(accent: AppAccent)

    /** Whether the bottom navigation bar shows text labels under its icons. */
    val showBarLabels: StateFlow<Boolean>
    fun setShowBarLabels(show: Boolean)

    /** Whether the feed compose button renders as an expanded (extended) FAB instead of the default one. */
    val expandedFab: StateFlow<Boolean>
    fun setExpandedFab(expanded: Boolean)

    /**
     * "Away" auto-delete window in months (3 / 6 / 12), or 0 when the feature is off.
     * When set, an account left away for the chosen amount of time is permanently deleted.
     */
    val awayMonths: StateFlow<Int>
    fun setAwayMonths(months: Int)
}
