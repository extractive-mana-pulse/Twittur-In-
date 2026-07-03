package com.example.twitturin.home.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.appLanguage
import com.example.twitturin.core.extensions.appThemeDialog
import com.example.twitturin.core.extensions.isNetworkAvailable
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.home.presentation.screens.HomeScreen
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        if (requireContext().isNetworkAvailable()) {
            profileViewModel.getUserCredentials(userId)
            tweetViewModel.getTweet(ShimmerFrameLayout(requireContext()))
        } else {
            findNavController().navigate(R.id.noInternetFragment)
        }
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(
                    profileViewModel = profileViewModel,
                    tweetViewModel = tweetViewModel,
                    onAddPost = { findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment) },
                    onTweetClick = { tweet ->
                        findNavController().navigate(
                            R.id.detailFragment,
                            Bundle().apply {
                                putParcelable("tweet", tweet)
                                putBoolean("activateEditText", false)
                            }
                        )
                    },
                    onTweetShare = { tweet ->
                        requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}")
                    },
                    onLanguage = { requireActivity().appLanguage() },
                    onTheme = { requireActivity().appThemeDialog() },
                    onTimetable = { findNavController().navigate(R.id.action_homeFragment_to_webViewFragment) },
                    onProfile = { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) },
                    onFeedback = { findNavController().navigate(R.id.action_homeFragment_to_feedbackFragment) },
                    onSettings = { findNavController().navigate(R.id.action_homeFragment_to_settingsFragment) }
                )
            }
        }
    }
}
