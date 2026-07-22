package com.example.twitturin.feature.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.LottieAsset
import com.example.twitturin.core.designsystem.component.LottieEmptyState
import com.example.twitturin.core.designsystem.component.SearchField
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRoot(
    onNavigateToProfile: (String) -> Unit,
    onMenu: (() -> Unit)? = null,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchEvent.NavigateToProfile -> onNavigateToProfile(event.userId)
            is SearchEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    SearchScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        onMenu = onMenu,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onMenu: (() -> Unit)? = null,
) {
    val strings = LocalStrings.current
    // The mic dictates a query and runs it immediately.
    val speechLauncher = rememberSpeechInput { phrase ->
        onAction(SearchAction.OnQueryChange(phrase))
        onAction(SearchAction.OnSearch(phrase))
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
                if (onMenu != null) {
                    IconButton(onClick = onMenu) {
                        Icon(TwitturIcons.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
                SearchField(
                    value = state.query,
                    onValueChange = { onAction(SearchAction.OnQueryChange(it)) },
                    onSearch = { onAction(SearchAction.OnSearch(state.query)) },
                    placeholder = strings.searchPeople,
                    onMicClick = speechLauncher,
                    modifier = Modifier.weight(1f).padding(start = if (onMenu != null) 4.dp else 12.dp),
                )
            }

            when {
                state.isLoading -> LoadingBox()

                // Pre-query: everyone on the platform, or the search-cat while that loads.
                !state.hasSearched -> if (state.suggestions.isEmpty()) {
                    LottieEmptyState(
                        asset = LottieAsset.SearchCat,
                        title = strings.findPeopleTitle,
                        subtitle = strings.findPeopleSubtitle,
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = strings.peopleOnTwitturin.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Hint,
                                modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 4.dp),
                            )
                        }
                        items(state.suggestions) { user ->
                            SearchUserRow(user = user, onClick = { onAction(SearchAction.OnUserClick(user.id)) })
                            HorizontalDivider()
                        }
                    }
                }

                state.results.isEmpty() -> LottieEmptyState(
                    asset = LottieAsset.GearSearch,
                    title = strings.noResultsTitle,
                    subtitle = strings.noResultsSubtitle,
                )

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.results) { user ->
                        SearchUserRow(user = user, onClick = { onAction(SearchAction.OnUserClick(user.id)) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchUserRow(user: SearchUserUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp)) {
            GradientAvatar(name = user.fullName, size = 48.dp)
            if (!user.profilePicture.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
            )
        }
    }
}
