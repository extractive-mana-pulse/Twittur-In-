package com.example.twitturin.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.domain.preferences.AppAccent
import com.example.twitturin.core.domain.preferences.ThemeMode
import com.example.twitturin.core.presentation.LocalStrings
import org.koin.compose.viewmodel.koinViewModel

const val TIMETABLE_URL = "https://ttpu.edupage.org/timetable/"

/** The selectable "away" auto-delete windows, in months. */
private val AWAY_OPTIONS = listOf(3, 6, 12)

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    onOpenFeedback: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val accent by viewModel.accent.collectAsStateWithLifecycle()
    val showLabels by viewModel.showBarLabels.collectAsStateWithLifecycle()
    val expandedFab by viewModel.expandedFab.collectAsStateWithLifecycle()
    val awayMonths by viewModel.awayMonths.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val strings = LocalStrings.current

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAccentDialog by remember { mutableStateOf(false) }
    var showAwaySheet by remember { mutableStateOf(false) }

    SettingsScreen(
        themeLabel = when (themeMode) {
            ThemeMode.SYSTEM -> strings.themeSystem
            ThemeMode.LIGHT -> strings.themeLight
            ThemeMode.DARK -> strings.themeDark
        },
        languageLabel = language.displayName,
        accent = accent,
        showLabels = showLabels,
        expandedFab = expandedFab,
        awayMonths = awayMonths,
        onBack = onBack,
        onThemeClick = { showThemeDialog = true },
        onLanguageClick = { showLanguageDialog = true },
        onAccentClick = { showAccentDialog = true },
        onToggleLabels = viewModel::setShowBarLabels,
        onToggleExpandedFab = viewModel::setExpandedFab,
        onAwayClick = { showAwaySheet = true },
        onTimetableClick = { uriHandler.openUri(TIMETABLE_URL) },
        onFeedbackClick = onOpenFeedback,
    )

    if (showThemeDialog) {
        ThemeDialog(current = themeMode, onSelect = { viewModel.setThemeMode(it) }, onDismiss = { showThemeDialog = false })
    }
    if (showLanguageDialog) {
        LanguageDialog(current = language, onSelect = { viewModel.setLanguage(it) }, onDismiss = { showLanguageDialog = false })
    }
    if (showAccentDialog) {
        AccentDialog(current = accent, onSelect = { viewModel.setAccent(it) }, onDismiss = { showAccentDialog = false })
    }
    if (showAwaySheet) {
        AwayModeSheet(
            current = awayMonths,
            onSelect = { months ->
                viewModel.setAwayMonths(months)
                showAwaySheet = false
            },
            onDismiss = { showAwaySheet = false },
        )
    }
}

@Composable
fun SettingsScreen(
    themeLabel: String,
    languageLabel: String,
    accent: AppAccent,
    showLabels: Boolean,
    expandedFab: Boolean,
    awayMonths: Int,
    onBack: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onAccentClick: () -> Unit,
    onToggleLabels: (Boolean) -> Unit,
    onToggleExpandedFab: (Boolean) -> Unit,
    onAwayClick: () -> Unit,
    onTimetableClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = strings.settings, onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionLabel(strings.appearance)
            SettingRow(TwitturIcons.Settings, strings.theme, themeLabel, onThemeClick)
            SettingRow(TwitturIcons.Translate, strings.language, languageLabel, onLanguageClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel(strings.customization)
            // Accent colour with a trailing swatch.
            SettingRow(
                icon = TwitturIcons.Visibility,
                title = strings.accentColour,
                subtitle = accent.displayName,
                onClick = onAccentClick,
                trailing = {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(accent.argb)),
                    )
                },
            )
            SwitchRow(TwitturIcons.Home, strings.showNavLabels, strings.showNavLabelsSub, showLabels, onToggleLabels)
            SwitchRow(TwitturIcons.Add, strings.composeButton, strings.composeButtonSub, expandedFab, onToggleExpandedFab)
            SettingRow(
                icon = TwitturIcons.Account,
                title = strings.awayMode,
                subtitle = if (awayMonths in AWAY_OPTIONS) {
                    strings.awayActiveSub.replace("%1", monthsLabel(awayMonths))
                } else {
                    strings.awayOffSub
                },
                onClick = onAwayClick,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel(strings.more)
            SettingRow(TwitturIcons.Bookmark, strings.timetable, strings.timetableSub, onTimetableClick)
            SettingRow(TwitturIcons.Feedback, strings.sendFeedback, strings.sendFeedbackSub, onFeedbackClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel(strings.about)
            SettingRow(TwitturIcons.Info, strings.about, strings.aboutSub, onClick = {})
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Resolves the localized "3 months" style label for an away window. */
@Composable
private fun monthsLabel(months: Int): String {
    val strings = LocalStrings.current
    return when (months) {
        3 -> strings.months3
        6 -> strings.months6
        else -> strings.months12
    }
}

/**
 * Away-mode picker: title + explanation, then the three auto-delete windows. Tapping the already
 * selected window turns the feature off again; an explicit "Turn off" action appears when active.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AwayModeSheet(
    current: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 28.dp)) {
            Text(
                text = strings.awaySheetTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = strings.awaySheetBody,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
            AWAY_OPTIONS.forEach { months ->
                val selected = months == current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(if (selected) 0 else months) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { onSelect(if (selected) 0 else months) },
                        colors = RadioButtonDefaults.colors(selectedColor = Brand),
                    )
                    Text(
                        text = monthsLabel(months),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            if (current in AWAY_OPTIONS) {
                TextButton(onClick = { onSelect(0) }, modifier = Modifier.align(Alignment.End)) {
                    Text(strings.turnOff, color = SecondaryText)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Hint,
        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    tint: Color = Ink,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = if (tint == Ink) Brand else tint, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = tint)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
            }
        }
        trailing()
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Brand, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnBrand,
                checkedTrackColor = Brand,
            ),
        )
    }
}
