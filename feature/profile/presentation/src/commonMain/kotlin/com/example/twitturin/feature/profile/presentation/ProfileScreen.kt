package com.example.twitturin.feature.profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onEditProfile: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfileEvent.LoggedOut, ProfileEvent.AccountDeleted -> onSignedOut()
            is ProfileEvent.ShowError -> pendingError = event.message
            ProfileEvent.Saved -> Unit // handled by the edit screen
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    ProfileScreen(
        state = state,
        onEditProfile = onEditProfile,
        onLogout = viewModel::logout,
        onDeleteAccount = viewModel::deleteAccount,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = state.user?.username?.let { "@$it" } ?: "Profile") },
                actions = {
                    TextButton(onClick = { menuOpen = true }) { Text(text = "Menu") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Edit profile") },
                            onClick = { menuOpen = false; onEditProfile() },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Log out") },
                            onClick = { menuOpen = false; showLogoutConfirm = true },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Delete account") },
                            onClick = { menuOpen = false; showDeleteConfirm = true },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val user = state.user
        when {
            state.isLoading && user == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            user == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(text = "Could not load your profile", style = MaterialTheme.typography.bodyLarge)
                }
            }

            else -> ProfileContent(
                user = user,
                onAvatarClick = { if (user.profilePicture != null) showFullImage = true },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showLogoutConfirm) {
        ConfirmDialog(
            title = "Log out",
            message = "Are you sure you want to log out?",
            confirmLabel = "Log out",
            onConfirm = { showLogoutConfirm = false; onLogout() },
            onDismiss = { showLogoutConfirm = false },
        )
    }
    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete account",
            message = "This permanently deletes your account. Continue?",
            confirmLabel = "Delete",
            onConfirm = { showDeleteConfirm = false; onDeleteAccount() },
            onDismiss = { showDeleteConfirm = false },
        )
    }
    if (showFullImage && state.user?.profilePicture != null) {
        FullScreenImage(url = state.user.profilePicture, onDismiss = { showFullImage = false })
    }
}

@Composable
private fun ProfileContent(
    user: ProfileUi,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        AsyncImage(
            model = user.profilePicture,
            contentDescription = null,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = "@${user.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (user.bio.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.bio, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        listOfNotNull(user.kind, user.studentId, user.country.ifBlank { null }).forEach { detail ->
            Text(text = detail, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(text = "${user.followersCount} followers", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${user.followingCount} following", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(text = confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
    )
}

@Composable
private fun FullScreenImage(url: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}
