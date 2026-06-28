package com.example.twitturin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.twitturin.core.designsystem.theme.TwitturTheme
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.feature.auth.presentation.kind.KindScreen
import com.example.twitturin.feature.auth.presentation.login.SignInRoot
import com.example.twitturin.feature.auth.presentation.registration.ProfessorRegistrationRoot
import com.example.twitturin.feature.auth.presentation.registration.StudentRegistrationRoot
import com.example.twitturin.feature.auth.presentation.stayin.StayInRoot
import com.example.twitturin.feature.home.presentation.HomeRoot
import com.example.twitturin.feature.profile.presentation.EditProfileRoot
import com.example.twitturin.feature.profile.presentation.ProfileRoot
import com.example.twitturin.feature.notification.presentation.NotificationRoot
import com.example.twitturin.feature.notification.presentation.PatchNoteRoot
import com.example.twitturin.feature.search.presentation.SearchRoot
import com.example.twitturin.feature.tweet.presentation.feed.FeedRoot
import com.example.twitturin.feature.tweet.presentation.post.PostTweetRoot
import com.example.twitturin.navigation.ComposeTweetRoute
import com.example.twitturin.navigation.EditProfileRoute
import com.example.twitturin.navigation.FeedRoute
import com.example.twitturin.navigation.HomeRoute
import com.example.twitturin.navigation.KindRoute
import com.example.twitturin.navigation.NotificationRoute
import com.example.twitturin.navigation.PatchNoteRoute
import com.example.twitturin.navigation.ProfessorRegistrationRoute
import com.example.twitturin.navigation.ProfileRoute
import com.example.twitturin.navigation.SearchRoute
import com.example.twitturin.navigation.SignInRoute
import com.example.twitturin.navigation.StayInRoute
import com.example.twitturin.navigation.StudentRegistrationRoute
import org.koin.compose.koinInject

/**
 * Root composable shared across Android, iOS, Desktop and (later) Web.
 * Theme + type-safe NavHost. Features are added as routes as each is ported into Track A.
 */
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val sessionSource = koinInject<SessionSource>()
    // Auth-first: start on Home only if the user previously chose "stay signed in".
    val startDestination: Any = remember { if (sessionSource.isRemembered()) HomeRoute else SignInRoute }

    TwitturTheme {
        val navController = rememberNavController()
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
                    // Back to sign-in so the new user can log in.
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

            // --- home hub ---
            composable<HomeRoute> {
                HomeRoot(
                    onOpenFeed = { navController.navigate(FeedRoute) },
                    onOpenSearch = { navController.navigate(SearchRoute) },
                    onOpenNotifications = { navController.navigate(NotificationRoute) },
                    onOpenProfile = { navController.navigate(ProfileRoute) },
                    onLoggedOut = {
                        navController.navigate(SignInRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                )
            }

            // --- profile ---
            composable<ProfileRoute> {
                ProfileRoot(
                    onEditProfile = { navController.navigate(EditProfileRoute) },
                    // logout / account-deletion both return to the auth flow.
                    onSignedOut = {
                        navController.navigate(SignInRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                )
            }
            composable<EditProfileRoute> {
                EditProfileRoot(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }

            // --- tweet: feed + compose ---
            composable<FeedRoute> {
                FeedRoot(
                    onBack = { navController.popBackStack() },
                    onOpenCompose = { navController.navigate(ComposeTweetRoute) },
                    // Tweet detail not yet ported — wired when :feature:detail lands.
                    onOpenTweet = {},
                )
            }
            composable<ComposeTweetRoute> {
                PostTweetRoot(
                    onBack = { navController.popBackStack() },
                    onPosted = { navController.popBackStack() },
                )
            }

            // --- feature destinations reachable from Home ---
            composable<SearchRoute> {
                SearchRoot(
                    // Profile feature not yet ported — wired when :feature:profile lands.
                    onNavigateToProfile = {},
                )
            }
            composable<NotificationRoute> {
                NotificationRoot(
                    onNavigateToPatchNote = { navController.navigate(PatchNoteRoute) },
                )
            }
            composable<PatchNoteRoute> {
                PatchNoteRoot(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
