package com.example.twitturin.feature.profile.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.designsystem.component.ConfirmDialog
import com.example.twitturin.core.designsystem.component.FollowButton
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.component.KindBadge
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.SecondaryButton
import com.example.twitturin.core.designsystem.component.TwitturTopBarMore
import com.example.twitturin.core.designsystem.component.TypedConfirmDialog
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Where "Report" complaints go — opens the platform email app with this recipient. */
private const val REPORT_EMAIL = "mukhammadaminsalokhiddinov@gmail.com"

/**
 * Profile screen. [userId] = null shows the signed-in user's own profile (edit/delete/logout);
 * any other id shows that user's profile with a follow/unfollow button and a report action.
 */
@Composable
fun ProfileRoot(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenFollowers: (String) -> Unit,
    onOpenFollowing: (String) -> Unit,
    onSignedOut: () -> Unit,
    userId: String? = null,
    onShareProfile: (userId: String) -> Unit = {},
    tabsContent: @Composable (userId: String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel { parametersOf(userId) },
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
        onBack = onBack,
        onEditProfile = onEditProfile,
        onOpenFollowers = onOpenFollowers,
        onOpenFollowing = onOpenFollowing,
        onLogout = viewModel::logout,
        onDeleteAccount = viewModel::deleteAccount,
        onToggleFollow = viewModel::toggleFollow,
        onShareProfile = onShareProfile,
        tabsContent = tabsContent,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenFollowers: (String) -> Unit,
    onOpenFollowing: (String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onToggleFollow: () -> Unit = {},
    onShareProfile: (userId: String) -> Unit = {},
    tabsContent: @Composable (userId: String) -> Unit = {},
) {
    val strings = LocalStrings.current
    val uriHandler = LocalUriHandler.current
    var menuOpen by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDeleteTypedConfirm by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    fun reportUser() {
        val username = state.user?.username.orEmpty()
        runCatching {
            uriHandler.openUri("mailto:$REPORT_EMAIL?subject=" + "Report user @$username".replace(" ", "%20"))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            // Title is always "Profile"; no nav drawer here (only the home screen has it).
            TwitturTopBarMore(
                title = strings.profile,
                menuExpanded = menuOpen,
                onMenuClick = { menuOpen = true },
                onMenuDismiss = { menuOpen = false },
                onBack = onBack,
                extraActions = {
                    state.user?.let { user ->
                        IconButton(onClick = { onShareProfile(user.id) }) {
                            Icon(TwitturIcons.Share, contentDescription = strings.shareProfile, tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                menu = {
                    if (state.isMe) {
                        DropdownMenuItem(
                            text = { Text(strings.deleteAccount, color = Danger) },
                            onClick = { menuOpen = false; showDeleteConfirm = true },
                        )
                        DropdownMenuItem(
                            text = { Text(strings.logOut) },
                            onClick = { menuOpen = false; showLogoutConfirm = true },
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(strings.report) },
                            onClick = { menuOpen = false; reportUser() },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val user = state.user
        when {
            state.isLoading && user == null -> Box(Modifier.fillMaxSize().padding(innerPadding)) { LoadingBox() }

            user == null -> Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = strings.couldNotLoadProfile, style = MaterialTheme.typography.bodyLarge)
            }

            else -> Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                ProfileHeader(
                    user = user,
                    isMe = state.isMe,
                    isFollowing = state.isFollowing,
                    onAvatarClick = { if (user.profilePicture != null) showFullImage = true },
                    onEditProfile = onEditProfile,
                    onToggleFollow = onToggleFollow,
                    onOpenFollowers = { onOpenFollowers(user.id) },
                    onOpenFollowing = { onOpenFollowing(user.id) },
                )
                // Posts / Replies / Likes tabs (slotted in from :feature:tweet by :composeApp).
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    tabsContent(user.id)
                }
            }
        }
    }

    if (showLogoutConfirm) {
        ConfirmDialog(
            title = strings.logOut,
            message = strings.logOutMessage,
            confirmLabel = strings.logOut,
            dismissLabel = strings.no,
            onConfirm = { showLogoutConfirm = false; onLogout() },
            onDismiss = { showLogoutConfirm = false },
        )
    }
    // Account deletion is a two-step confirmation: a plain warning, then a typed username gate.
    if (showDeleteConfirm) {
        ConfirmDialog(
            title = strings.deleteAccountTitle,
            message = strings.deleteAccountMessage,
            confirmLabel = strings.continueLabel,
            dismissLabel = strings.cancel,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                showDeleteTypedConfirm = true
            },
            onDismiss = { showDeleteConfirm = false },
        )
    }
    if (showDeleteTypedConfirm) {
        TypedConfirmDialog(
            title = strings.confirmUsernameTitle,
            message = strings.confirmUsernameMessage,
            expectedText = state.user?.username.orEmpty(),
            confirmLabel = strings.confirm,
            dismissLabel = strings.cancel,
            placeholder = strings.usernamePlaceholder,
            onConfirm = {
                showDeleteTypedConfirm = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteTypedConfirm = false },
        )
    }
    if (showFullImage && state.user?.profilePicture != null) {
        FullScreenImage(url = state.user.profilePicture, onDismiss = { showFullImage = false })
    }
}

@Composable
private fun ProfileHeader(
    user: ProfileUi,
    isMe: Boolean,
    isFollowing: Boolean?,
    onAvatarClick: () -> Unit,
    onEditProfile: () -> Unit,
    onToggleFollow: () -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Box(modifier = Modifier.size(88.dp).clickable(onClick = onAvatarClick)) {
            GradientAvatar(name = user.fullName, size = 88.dp)
            if (!user.profilePicture.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = null,
                    modifier = Modifier.size(88.dp).clip(CircleShape),
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = user.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            user.kind?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.size(8.dp))
                KindBadge(kind = it)
            }
        }
        Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
        if (user.bio.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = user.bio, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        listOfNotNull(user.studentId?.takeIf { it.isNotBlank() }, user.country.ifBlank { null }).forEach {
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StatLabel(count = user.followersCount, label = strings.followers.lowercase(), onClick = onOpenFollowers)
            StatLabel(count = user.followingCount, label = strings.following.lowercase(), onClick = onOpenFollowing)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isMe) {
            SecondaryButton(text = strings.editProfile, onClick = onEditProfile)
        } else if (isFollowing != null) {
            FollowButton(
                following = isFollowing,
                onClick = onToggleFollow,
                followText = strings.follow,
                followingText = strings.unfollow,
            )
        }
    }
}

@Composable
private fun StatLabel(count: Int, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable(onClick = onClick)) {
        Text(text = "$count ", style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
    }
}

@Composable
private fun FullScreenImage(url: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brand.copy(alpha = 0.06f))
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
