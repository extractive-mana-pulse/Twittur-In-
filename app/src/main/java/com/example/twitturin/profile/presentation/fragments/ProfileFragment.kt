package com.example.twitturin.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.defaultDialog
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.profile.presentation.screens.ProfileScreen
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val stayInViewModel by viewModels<StayInViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val tweetViewModel by viewModels<TweetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    tweetViewModel = tweetViewModel,
                    userId = userId,
                    onShareProfile = { findNavController().navigate(R.id.action_profileFragment_to_shareProfileBottomSheetFragment) },
                    onEditProfile = { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) },
                    onLogout = { confirmLogout() },
                    onDeleteAccount = { confirmDelete(userId) },
                    onFollowers = { findNavController().navigate(R.id.action_profileFragment_to_followersListFragment) },
                    onFollowing = { findNavController().navigate(R.id.action_profileFragment_to_followingListFragment) },
                    onTweetClick = { tweet ->
                        findNavController().navigate(
                            R.id.detailFragment,
                            Bundle().apply { putParcelable("tweet", tweet) }
                        )
                    },
                    onTweetShare = { tweet ->
                        requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}")
                    }
                )
            }
        }
    }

    private fun confirmLogout() {
        defaultDialog(
            getString(R.string.logout),
            getString(R.string.logout_message),
            actionYesClicked = {
                SessionManager(requireContext()).clearToken()
                stayInViewModel.setUserLoggedIn(false)
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            },
            actionNoClicked = {}
        )
    }

    private fun confirmDelete(userId: String) {
        defaultDialog(
            getString(R.string.delete_account_title),
            getString(R.string.delete_account_message),
            actionYesClicked = {
                profileViewModel.deleteUser(userId, "Bearer ${SessionManager(requireContext()).getToken()}")
                viewLifecycleOwner.lifecycleScope.launch {
                    profileViewModel.deleteResult.collectLatest { result ->
                        if (result is AccountDelete.Success) {
                            SessionManager(requireContext()).clearToken()
                            stayInViewModel.setUserLoggedIn(false)
                            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                        }
                    }
                }
            },
            actionNoClicked = {}
        )
    }
}
