package com.example.twitturin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.twitturin.core.designsystem.component.BottomNavItem
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.designsystem.theme.TwitturTheme
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.preferences.AppPreferences
import com.example.twitturin.core.domain.preferences.ThemeMode
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.stringsFor
import com.example.twitturin.feature.auth.presentation.kind.KindScreen
import com.example.twitturin.feature.auth.presentation.login.SignInRoot
import com.example.twitturin.feature.auth.presentation.registration.ProfessorRegistrationRoot
import com.example.twitturin.feature.auth.presentation.registration.StudentRegistrationRoot
import com.example.twitturin.feature.auth.presentation.stayin.StayInRoot
import com.example.twitturin.feature.follow.presentation.FollowersRoot
import com.example.twitturin.feature.follow.presentation.FollowingRoot
import com.example.twitturin.feature.notification.presentation.NotificationRoot
import com.example.twitturin.feature.notification.presentation.PatchNoteRoot
import com.example.twitturin.feature.profile.presentation.EditProfileRoot
import com.example.twitturin.feature.profile.presentation.ProfileRoot
import com.example.twitturin.feature.profile.presentation.ProfileUi
import com.example.twitturin.feature.profile.presentation.ProfileViewModel
import com.example.twitturin.feature.search.presentation.SearchRoot
import com.example.twitturin.feature.settings.presentation.FeedbackRoot
import com.example.twitturin.feature.settings.presentation.SettingsRoot
import com.example.twitturin.feature.tweet.presentation.detail.DetailRoot
import com.example.twitturin.feature.tweet.presentation.feed.FeedRoot
import com.example.twitturin.feature.tweet.presentation.likes.LikesListRoot
import com.example.twitturin.feature.tweet.presentation.post.PostTweetRoot
import com.example.twitturin.feature.timetable.presentation.TimetableRoot
import com.example.twitturin.feature.timetable.presentation.picker.TimetableSubjectPickerRoot
import com.example.twitturin.feature.tweet.presentation.profile.UserContentTabs
import com.example.twitturin.navigation.AppNavigation
import com.example.twitturin.navigation.ComposeTweetRoute
import com.example.twitturin.navigation.RailAction
import com.example.twitturin.navigation.DetailRoute
import com.example.twitturin.navigation.EditProfileRoute
import com.example.twitturin.navigation.FeedbackRoute
import com.example.twitturin.navigation.FollowersRoute
import com.example.twitturin.navigation.FollowingRoute
import com.example.twitturin.navigation.HomeRoute
import com.example.twitturin.navigation.KindRoute
import com.example.twitturin.navigation.LikesListRoute
import com.example.twitturin.navigation.PatchNoteRoute
import com.example.twitturin.navigation.ProfessorRegistrationRoute
import com.example.twitturin.navigation.ProfileRoute
import com.example.twitturin.navigation.SettingsRoute
import com.example.twitturin.navigation.SignInRoute
import com.example.twitturin.navigation.StayInRoute
import com.example.twitturin.navigation.StudentRegistrationRoute
import com.example.twitturin.navigation.TimetableSubjectPickerRoute
import com.example.twitturin.platform.rememberRecentDeviceImages
import com.example.twitturin.platform.rememberShareHandler
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private const val APP_VERSION = "Version 2.0"

private fun tweetShareUrl(tweetId: String) = "https://twitturin.onrender.com/tweets/$tweetId"
private fun profileShareUrl(userId: String) = "https://twitturin.onrender.com/users/$userId"

/**
 * Root composable shared across Android, iOS, Desktop and (later) Web.
 * Theme + strings + type-safe NavHost. Features are added as routes as each is ported into Track A.
 */
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val sessionSource = koinInject<SessionSource>()
    val appPreferences = koinInject<AppPreferences>()
    val themeMode by appPreferences.themeMode.collectAsState()
    val accent by appPreferences.accent.collectAsState()
    val language by appPreferences.language.collectAsState()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    // Auth-first: start on Home only if the user previously chose "stay signed in".
    val startDestination: Any = remember { if (sessionSource.isRemembered()) HomeRoute else SignInRoute }

    TwitturTheme(darkTheme = darkTheme, accent = Color(accent.argb)) {
        // Reactive i18n: switching the language in Settings re-translates every screen instantly.
        CompositionLocalProvider(LocalStrings provides stringsFor(language)) {
            val navController = rememberNavController()
            val share = rememberShareHandler()

            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                // --- auth subflow ---
                composable<SignInRoute> {
                    SignInRoot(
                        onSignedIn = { navController.navigate(StayInRoute) },
                        onSignUp = { navController.navigate(KindRoute) },
                    )
                }
                composable<KindRoute> {
                    KindScreen(
                        onBack = { navController.popBackStack() },
                        onProfessor = { navController.navigate(ProfessorRegistrationRoute) },
                        onStudent = { navController.navigate(StudentRegistrationRoute) },
                    )
                }
                composable<StudentRegistrationRoute> {
                    StudentRegistrationRoot(
                        onBack = { navController.popBackStack() },
                        onRegistered = { navController.popBackStack(SignInRoute, inclusive = false) },
                    )
                }
                composable<ProfessorRegistrationRoute> {
                    ProfessorRegistrationRoot(
                        onBack = { navController.popBackStack() },
                        onRegistered = { navController.popBackStack(SignInRoute, inclusive = false) },
                    )
                }
                composable<StayInRoute> {
                    StayInRoot(
                        onFinished = {
                            navController.navigate(HomeRoute) {
                                popUpTo(SignInRoute) { inclusive = true }
                            }
                        },
                    )
                }

                // --- home: bottom-nav shell (Feed / Search / Notifications / Messages) + drawer ---
                composable<HomeRoute> {
                    MainShell(navController = navController, share = share)
                }

                // --- profile: own (drawer/header) or any user's (avatars, search, followers…) ---
                composable<ProfileRoute> { entry ->
                    val route = entry.toRoute<ProfileRoute>()
                    ProfileRoot(
                        userId = route.userId,
                        onBack = { navController.popBackStack() },
                        onEditProfile = { navController.navigate(EditProfileRoute) },
                        onOpenFollowers = { userId -> navController.navigate(FollowersRoute(userId)) },
                        onOpenFollowing = { userId -> navController.navigate(FollowingRoute(userId)) },
                        onSignedOut = {
                            sessionSource.clear()
                            navController.navigate(SignInRoute) { popUpTo(HomeRoute) { inclusive = true } }
                        },
                        onShareProfile = { userId -> share(profileShareUrl(userId)) },
                        tabsContent = { userId ->
                            UserContentTabs(
                                userId = userId,
                                onOpenTweet = { id, focus -> navController.navigate(DetailRoute(id, focus)) },
                                onShare = { id -> share(tweetShareUrl(id)) },
                            )
                        },
                    )
                }
                composable<FollowersRoute> { entry ->
                    val route = entry.toRoute<FollowersRoute>()
                    FollowersRoot(
                        userId = route.userId,
                        onBack = { navController.popBackStack() },
                        onOpenProfile = { userId -> navController.navigate(ProfileRoute(userId)) },
                    )
                }
                composable<FollowingRoute> { entry ->
                    val route = entry.toRoute<FollowingRoute>()
                    FollowingRoot(
                        userId = route.userId,
                        onBack = { navController.popBackStack() },
                        onOpenProfile = { userId -> navController.navigate(ProfileRoute(userId)) },
                    )
                }
                composable<EditProfileRoute> {
                    EditProfileRoot(
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                // --- tweet: compose/edit + detail (replies) + likes (feed lives in the shell) ---
                composable<ComposeTweetRoute> { entry ->
                    val route = entry.toRoute<ComposeTweetRoute>()
                    PostTweetRoot(
                        onBack = { navController.popBackStack() },
                        onPosted = { navController.popBackStack() },
                        tweetId = route.tweetId,
                        initialText = route.initialText,
                        recentImages = rememberRecentDeviceImages(5),
                    )
                }
                composable<DetailRoute> { entry ->
                    val route = entry.toRoute<DetailRoute>()
                    DetailRoot(
                        tweetId = route.tweetId,
                        focusReply = route.focusReply,
                        onBack = { navController.popBackStack() },
                        onOpenTweet = { tweetId -> navController.navigate(DetailRoute(tweetId)) },
                        onOpenLikes = { tweetId -> navController.navigate(LikesListRoute(tweetId)) },
                        onShare = { tweetId -> share(tweetShareUrl(tweetId)) },
                        onEdit = { tweetId, content -> navController.navigate(ComposeTweetRoute(tweetId, content)) },
                        onOpenProfile = { userId -> navController.navigate(ProfileRoute(userId)) },
                    )
                }
                composable<LikesListRoute> { entry ->
                    val route = entry.toRoute<LikesListRoute>()
                    LikesListRoot(
                        tweetId = route.tweetId,
                        onBack = { navController.popBackStack() },
                    )
                }

                composable<PatchNoteRoute> {
                    PatchNoteRoot(onBack = { navController.popBackStack() })
                }

                // --- settings + feedback (reached from the drawer) ---
                composable<SettingsRoute> {
                    SettingsRoot(
                        onBack = { navController.popBackStack() },
                        onOpenFeedback = { navController.navigate(FeedbackRoute) },
                    )
                }
                composable<FeedbackRoute> {
                    FeedbackRoot(onBack = { navController.popBackStack() })
                }

                // --- timetable (beta): subject picker is a pushed route (its own top bar/search/save) ---
                composable<TimetableSubjectPickerRoute> {
                    TimetableSubjectPickerRoot(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

/**
 * Post-login shell. A persistent bottom bar (Home / Search / Notifications / Messages) plus a
 * navigation drawer that opens from the **home** tab's profile-picture button. Profile, Timetable
 * and Feedback/Settings live in the drawer; the four tabs swap inside this single back-stack entry
 * so the bar never disappears. Deeper screens push on top via [navController].
 */
@Composable
private fun MainShell(navController: NavHostController, share: (String) -> Unit) {
    var tab by rememberSaveable { mutableStateOf(0) }
    val appPreferences = koinInject<AppPreferences>()
    val themeMode by appPreferences.themeMode.collectAsState()
    val showLabels by appPreferences.showBarLabels.collectAsState()
    val expandedFab by appPreferences.expandedFab.collectAsState()
    val strings = LocalStrings.current

    val profileViewModel: ProfileViewModel = koinViewModel()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val me: ProfileUi? = profileState.user

    // Sun/moon toggle: flips between explicit light and dark (resolving SYSTEM first).
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val toggleTheme = {
        appPreferences.setThemeMode(if (isDark) ThemeMode.LIGHT else ThemeMode.DARK)
    }

    val tabs = listOf(
        BottomNavItem(TwitturIcons.Home, strings.home),
        BottomNavItem(TwitturIcons.Search, strings.search),
        BottomNavItem(TwitturIcons.Notifications, strings.notifications),
        BottomNavItem(TwitturIcons.Mail, strings.messages),
        BottomNavItem(TwitturIcons.Bookmark, strings.timetable, badge = strings.timetableBetaBadge),
    )

    AppNavigation(
        tabs = tabs,
        selectedIndex = tab,
        onSelect = { tab = it },
        showLabels = showLabels,
        gesturesEnabled = tab == 0, // drawer only reachable from home
        // Desktop rail (no drawer there): the drawer's secondary destinations, pinned bottom.
        // Timetable now lives as tab 4 (was a raw web-link shortcut here) — both entry points
        // just select that tab instead of leaving the app.
        railActions = listOf(
            RailAction(TwitturIcons.Account, strings.profile) { navController.navigate(ProfileRoute()) },
            RailAction(if (isDark) TwitturIcons.Sun else TwitturIcons.Moon, if (isDark) strings.lightMode else strings.darkMode) { toggleTheme() },
            RailAction(TwitturIcons.Feedback, strings.feedback) { navController.navigate(FeedbackRoute) },
            RailAction(TwitturIcons.Settings, strings.settings) { navController.navigate(SettingsRoute) },
        ),
        drawerContent = { closeDrawer ->
            DrawerBody(
                user = me,
                isDark = isDark,
                onToggleTheme = toggleTheme,
                onProfile = { closeDrawer(); navController.navigate(ProfileRoute()) },
                onTimetable = { closeDrawer(); tab = 4 },
                onFeedback = { closeDrawer(); navController.navigate(FeedbackRoute) },
                onSettings = { closeDrawer(); navController.navigate(SettingsRoute) },
                onOpenFollowers = { closeDrawer(); me?.let { navController.navigate(FollowersRoute(it.id)) } },
                onOpenFollowing = { closeDrawer(); me?.let { navController.navigate(FollowingRoute(it.id)) } },
            )
        },
    ) { openDrawer ->
        when (tab) {
            0 -> FeedRoot(
                onOpenCompose = { navController.navigate(ComposeTweetRoute()) },
                onOpenTweet = { tweetId, focus -> navController.navigate(DetailRoute(tweetId, focus)) },
                onShare = { tweetId -> share(tweetShareUrl(tweetId)) },
                onOpenProfile = { userId -> navController.navigate(ProfileRoute(userId)) },
                expandedFab = expandedFab,
                onMenu = openDrawer,
                menuAvatarUrl = me?.profilePicture,
                menuAvatarName = me?.fullName ?: "",
            )
            1 -> SearchRoot(
                onNavigateToProfile = { userId -> navController.navigate(ProfileRoute(userId)) },
            )
            2 -> NotificationRoot(onNavigateToPatchNote = { navController.navigate(PatchNoteRoute) })
            3 -> MessagesScreen()
            else -> TimetableRoot(onOpenSubjectPicker = { navController.navigate(TimetableSubjectPickerRoute) })
        }
    }
}

/**
 * Navigation drawer body: tappable profile header (opens the profile screen) with the animated
 * sun/moon theme toggle in front of the avatar, follower/following counts, then the drawer items,
 * with the app version pinned to the bottom centre.
 */
@Composable
private fun ColumnScope.DrawerBody(
    user: ProfileUi?,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onProfile: () -> Unit,
    onTimetable: () -> Unit,
    onFeedback: () -> Unit,
    onSettings: () -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
) {
    val strings = LocalStrings.current
    // Header — tapping it opens the profile screen.
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 28.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).clickable(onClick = onProfile),
            ) {
                GradientAvatar(name = user?.fullName ?: "Twittur", size = 64.dp)
                if (user != null && !user.profilePicture.isNullOrBlank()) {
                    AsyncImage(
                        model = user.profilePicture,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            ThemeToggle(isDark = isDark, onToggle = onToggleTheme)
        }
        Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onProfile)) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = user?.fullName ?: "Twittur User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = user?.username?.let { "@$it" } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            DrawerStat(count = user?.followersCount ?: 0, label = strings.followers, onClick = onOpenFollowers)
            DrawerStat(count = user?.followingCount ?: 0, label = strings.following, onClick = onOpenFollowing)
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

    DrawerItem(TwitturIcons.Account, strings.profile, onProfile)
    DrawerItem(TwitturIcons.Bookmark, strings.timetable, onTimetable)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    DrawerItem(TwitturIcons.Feedback, strings.feedback, onFeedback)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    DrawerItem(TwitturIcons.Settings, strings.settings, onSettings)

    Spacer(Modifier.weight(1f))
    Text(
        text = APP_VERSION,
        style = MaterialTheme.typography.bodySmall,
        color = Hint,
        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

/**
 * Animated theme switch: the sun spins and shrinks away while the moon rolls in (and back).
 * Sun = light mode is active; tapping it turns on dark mode (and vice versa).
 */
@Composable
private fun ThemeToggle(isDark: Boolean, onToggle: () -> Unit) {
    val strings = LocalStrings.current
    val rotation by animateFloatAsState(
        targetValue = if (isDark) 360f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "theme-rotation",
    )
    IconButton(onClick = onToggle) {
        Box(modifier = Modifier.graphicsLayer { rotationZ = rotation }) {
            AnimatedContent(
                targetState = isDark,
                transitionSpec = {
                    (fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.4f))
                        .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.4f))
                },
                label = "theme-icon",
            ) { dark ->
                Icon(
                    imageVector = if (dark) TwitturIcons.Moon else TwitturIcons.Sun,
                    contentDescription = if (dark) strings.lightMode else strings.darkMode,
                    tint = if (dark) SecondaryText else Color(0xFFF4B400),
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

@Composable
private fun DrawerStat(count: Int, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable(onClick = onClick)) {
        Text(text = "$count ", style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
    }
}

@Composable
private fun DrawerItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}
