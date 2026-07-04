package com.example.twitturin.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.domain.preferences.AppAccent
import com.example.twitturin.core.domain.preferences.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

const val TIMETABLE_URL = "https://ttpu.edupage.org/timetable/"

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    onOpenFeedback: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val accent by viewModel.accent.collectAsStateWithLifecycle()
    val showLabels by viewModel.showBarLabels.collectAsStateWithLifecycle()
    val showFab by viewModel.showFab.collectAsStateWithLifecycle()
    val awayMode by viewModel.awayMode.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAccentDialog by remember { mutableStateOf(false) }

    SettingsScreen(
        themeLabel = when (themeMode) {
            ThemeMode.SYSTEM -> "System default"
            ThemeMode.LIGHT -> "Light"
            ThemeMode.DARK -> "Dark"
        },
        languageLabel = language.displayName,
        accent = accent,
        showLabels = showLabels,
        showFab = showFab,
        awayMode = awayMode,
        onBack = onBack,
        onThemeClick = { showThemeDialog = true },
        onLanguageClick = { showLanguageDialog = true },
        onAccentClick = { showAccentDialog = true },
        onToggleLabels = viewModel::setShowBarLabels,
        onToggleFab = viewModel::setShowFab,
        onToggleAway = viewModel::setAwayMode,
        onTimetableClick = { uriHandler.openUri(TIMETABLE_URL) },
        onFeedbackClick = onOpenFeedback,
        onLogout = onLogout,
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
}

@Composable
fun SettingsScreen(
    themeLabel: String,
    languageLabel: String,
    accent: AppAccent,
    showLabels: Boolean,
    showFab: Boolean,
    awayMode: Boolean,
    onBack: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onAccentClick: () -> Unit,
    onToggleLabels: (Boolean) -> Unit,
    onToggleFab: (Boolean) -> Unit,
    onToggleAway: (Boolean) -> Unit,
    onTimetableClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = "Settings", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionLabel("Appearance")
            SettingRow(TwitturIcons.Settings, "Theme", themeLabel, onThemeClick)
            SettingRow(TwitturIcons.Translate, "Language", languageLabel, onLanguageClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel("Customization")
            // Accent colour with a trailing swatch.
            SettingRow(
                icon = TwitturIcons.Visibility,
                title = "Accent colour",
                subtitle = accent.displayName,
                onClick = onAccentClick,
                trailing = {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(accent.argb)),
                    )
                },
            )
            SwitchRow(TwitturIcons.Home, "Show navigation labels", "Captions under the bottom bar icons", showLabels, onToggleLabels)
            SwitchRow(TwitturIcons.Add, "Show compose button", "The floating button on the feed", showFab, onToggleFab)
            SwitchRow(TwitturIcons.Account, "Away mode", "Show others that you're away", awayMode, onToggleAway)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel("More")
            SettingRow(TwitturIcons.Bookmark, "Timetable", "Open class schedule", onTimetableClick)
            SettingRow(TwitturIcons.Feedback, "Send feedback", "Report a bug or idea", onFeedbackClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SectionLabel("About")
            SettingRow(TwitturIcons.Info, "TwitturIn", "Version 2.0 · A Twitter-style campus app", onClick = {})

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingRow(TwitturIcons.Logout, "Log out", null, onLogout, tint = Danger)
            Spacer(Modifier.height(24.dp))
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
