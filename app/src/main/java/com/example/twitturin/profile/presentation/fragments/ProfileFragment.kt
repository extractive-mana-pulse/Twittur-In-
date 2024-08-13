package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisibleIf
import com.example.twitturin.core.extensions.converter
import com.example.twitturin.core.extensions.customDialog
import com.example.twitturin.core.extensions.defaultDialog
import com.example.twitturin.core.extensions.dialogSuccess
import com.example.twitturin.core.extensions.fullScreenImage
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.setupWithAdapter
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.toastCustom
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.ProfileUIEvent
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileUIViewModel
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val stayInViewModel by viewModels<StayInViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val profileUIViewModel by viewModels<ProfileUIViewModel>()
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("DiscouragedPrivateApi", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // NOTE: code related to viewPager2, vp2 cause an error: Fragment not found or no longer exist!
        binding.vp2.isSaveEnabled = false

        binding.apply {

            followersTv.setOnClickListener { profileUIViewModel.onFollowersPressed() }
            followingTv.setOnClickListener { profileUIViewModel.onFollowingPressed() }
            profileUserAvatar.setOnClickListener { profileUIViewModel.onAvatarPressed() }
            profileToolbar.setNavigationOnClickListener { profileUIViewModel.onBackPressed() }

            profileToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share_profile -> { findNavController().navigate(R.id.action_profileFragment_to_shareProfileBottomSheetFragment); true }
                    R.id.three_dot_menu -> {
                        PopupMenu(requireContext(), threeDotMenuView).apply {
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.logout -> {
                                        defaultDialog(
                                            title = resources.getString(R.string.logout),
                                            message = resources.getString(R.string.logout_message),
                                            actionYesClicked = {
                                                SessionManager(requireContext()).clearToken()
                                                stayInViewModel.setUserLoggedIn(false)
                                                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                            },
                                            actionNoClicked = {}
                                        )
                                        true
                                    }
                                    R.id.delete_account -> {
                                        defaultDialog(
                                            title = resources.getString(R.string.delete_account_title),
                                            message = resources.getString(R.string.delete_account_message),
                                            actionYesClicked = {
                                                customDialog(
                                                    actionCancelPressed = {},
                                                    actionEmailPressed = { requireActivity().toastCustom(resources.getString(R.string.success)) },
                                                    actionDeletePressed = {
                                                        profileViewModel.deleteUser(SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                                                        repeatOnStarted {
                                                            profileViewModel.deleteResult.collectLatest { result ->
                                                                when (result) {
                                                                    is AccountDelete.Success -> {
                                                                        SessionManager(requireContext()).clearToken()
                                                                        stayInViewModel.setUserLoggedIn(false)
                                                                        requireActivity().dialogSuccess()
                                                                        delay(3000)
                                                                        findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                                                    }
                                                                    is AccountDelete.Error -> { binding.profileRootLayout.snackbarError(binding.profileRootLayout, result.message, ""){} }
                                                                    is AccountDelete.Loading -> {}
                                                                }
                                                            }
                                                        }
                                                    },
                                                )
                                            },
                                            actionNoClicked = {}
                                        )
                                        true
                                    }
                                    R.id.edit_profile -> { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment); true }
                                    else -> false
                                }
                            }
                            inflate(R.menu.popup_menu_profile)
                            converter(this)
                        }
                        true
                    }
                    else -> false
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                profileUIViewModel.profileUiEvent.collect {
                    when(it){
                        ProfileUIEvent.OnBackPressed -> { findNavController().navigateUp() }
                        ProfileUIEvent.OnAvatarPressed -> { fullScreenImage(profileUserAvatar) }
                        ProfileUIEvent.OnFollowersPressed -> { findNavController().navigate(R.id.action_profileFragment_to_followersListFragment) }
                        ProfileUIEvent.OnFollowingPressed -> { findNavController().navigate(R.id.action_profileFragment_to_followingListFragment) }
                    }
                }
            }

            profileViewModel.getUserCredentials(SessionManager(requireContext()).getUserId()!!)
            repeatOnStarted {
                profileViewModel.getUserCredentials.collectLatest { result ->
                    profileShimmerLayout.startShimmer()
                    when (result) {
                        is UserCredentials.Success -> {

                            profileShimmerLayout.stopShimmer()
                            profileShimmerLayout.beGone()

                            result.user.apply {

                                profileKind.text = kind
                                profileDateTv.text = birthday
                                profileUsername.text = username
                                profileLocationTv.text = country
                                profileStudentIdTv.text = studentId
                                followingCounterTv.text = followingCount.toString()
                                followersCounterTv.text = followersCount.toString()
                                profileUserAvatar.loadImagesWithGlideExt(profilePicture)
                                profileBiography.text = (bio ?: resources.getString(R.string.empty_bio))
                                profileFullName.text = (fullName ?: resources.getString(R.string.default_user_fullname))
                                country?.let { profileLocationIcon.beVisibleIf(it.isNotEmpty()); profileLocationTv.beVisibleIf(it.isNotEmpty()) }
                            }
                        }
                        is UserCredentials.Error -> { profileRootLayout.snackbarError(profileRootLayout, result.message, ""){} }
                        is UserCredentials.Loading -> {}
                    }
                }
            }

            vp2.setupWithAdapter(
                tb,
                childFragmentManager,
                lifecycle,
                { fm, lc -> ProfileViewPagerAdapter(fm, lc) },
                { tab, pos ->
                    when (pos) {
                        0 -> tab.text = resources.getString(R.string.tweets)
                        1 -> tab.text = resources.getString(R.string.likes)
                    }
                }
            )
        }
    }
}