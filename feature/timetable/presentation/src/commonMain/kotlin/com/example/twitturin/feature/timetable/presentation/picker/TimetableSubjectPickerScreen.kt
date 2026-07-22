package com.example.twitturin.feature.timetable.presentation.picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.ActionSheetRow
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.SearchField
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimetableSubjectPickerRoot(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimetableSubjectPickerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TimetableSubjectPickerEvent.SavedAndClose -> onBack()
        }
    }

    TimetableSubjectPickerScreen(state = state, onAction = viewModel::onAction, onBack = onBack, modifier = modifier)
}

@Composable
fun TimetableSubjectPickerScreen(
    state: TimetableSubjectPickerState,
    onAction: (TimetableSubjectPickerAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    Scaffold(
        modifier = modifier,
        topBar = {
            BrandTopBar(
                title = strings.timetableSubjectsCta,
                onBack = onBack,
                actions = {
                    TextButton(onClick = { onAction(TimetableSubjectPickerAction.OnSave) }) {
                        Text(strings.save, color = Brand, fontWeight = FontWeight.Bold)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                Text(strings.timetablePickYourGroup, style = MaterialTheme.typography.labelLarge, color = SecondaryText)
                Spacer(Modifier.height(6.dp))
                SearchField(
                    value = state.cohortQuery,
                    onValueChange = { onAction(TimetableSubjectPickerAction.OnCohortQueryChange(it)) },
                    placeholder = strings.timetablePickYourGroup,
                )
                state.cohortSuggestions.forEach { cohort ->
                    ActionSheetRow(
                        icon = TwitturIcons.Bookmark,
                        label = cohort.name,
                        onClick = { onAction(TimetableSubjectPickerAction.OnCohortSelected(cohort.id)) },
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                SearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(TimetableSubjectPickerAction.OnSearchQueryChange(it)) },
                    placeholder = strings.timetableSearchSubjects,
                )
            }
            Text(
                text = "${state.selectedCount} / ${state.totalFollowableCount}",
                style = MaterialTheme.typography.labelMedium,
                color = SecondaryText,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp),
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                state.groups.forEach { group ->
                    item(key = "header:${group.baseName}") {
                        GroupHeaderRow(
                            group = group,
                            onToggle = { onAction(TimetableSubjectPickerAction.OnToggleGroup(group.rows.map { it.id })) },
                        )
                    }
                    items(group.rows, key = { it.id }) { row ->
                        SubjectCheckRow(row = row, onToggle = { onAction(TimetableSubjectPickerAction.OnToggleSubject(row.id)) })
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupHeaderRow(group: SubjectGroupUi, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val allChecked = group.rows.isNotEmpty() && group.rows.all { it.checked }
    val toggleState = when {
        allChecked -> ToggleableState.On
        group.rows.any { it.checked } -> ToggleableState.Indeterminate
        else -> ToggleableState.Off
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp)
            .padding(top = 14.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TriStateCheckbox(state = toggleState, onClick = onToggle, colors = CheckboxDefaults.colors(checkedColor = Brand))
        Text(
            text = group.baseName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun SubjectCheckRow(row: SubjectRowUi, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = row.checked, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = Brand))
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(text = row.label, style = MaterialTheme.typography.bodyMedium, color = Ink)
            Text(
                text = row.kindLabel.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
            )
        }
    }
}
