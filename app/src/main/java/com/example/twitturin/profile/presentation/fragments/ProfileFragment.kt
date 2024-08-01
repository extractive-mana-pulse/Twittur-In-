package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.converter
import com.example.twitturin.core.extensions.deleteDialogCodeWatcher
import com.example.twitturin.core.extensions.deleteDialogEmailWatcher
import com.example.twitturin.core.extensions.fullScreenImage
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.stateDisabled
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.profile.domain.model.User
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.ProfileUIEvent
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileUIViewModel
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
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
        binding.profileFragment = this

        /** @return NOTE code related to viewPager2, vp2 cause an error: Fragment not found or no longer exist! */
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
                                    R.id.logout -> { logoutDialog(); true }
                                    R.id.delete_account -> { deleteAccount(); true }
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
                                profileStudentIdTv.text = studentId
                                profileDateTv.text = birthday
                                profileUsername.text = username
                                followingCounterTv.text = followingCount.toString()
                                followersCounterTv.text = followersCount.toString()
                                profileUserAvatar.loadImagesWithGlideExt(profilePicture)
                                profileBiography.text = (bio ?: resources.getString(R.string.empty_bio))
                                profileFullName.text = (fullName ?: resources.getString(R.string.default_user_fullname))

                                // location
                                if (country.isNullOrEmpty()) {
                                    profileLocationIcon.beGone()
                                    profileLocationTv.beGone()
                                } else {
                                    profileLocationIcon.beVisible()
                                    profileLocationTv.beVisible()
                                }
                            }
                        }
                        is UserCredentials.Error -> { profileRootLayout.snackbarError(profileRootLayout, result.message, ""){} }
                        is UserCredentials.Loading -> {}
                    }
                }
            }

            vp2.adapter = ProfileViewPagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tb, vp2) { tab, pos ->
                when (pos) {
                    0 -> {
                        tab.text = resources.getString(R.string.tweets)
                    }
                    1 -> {
                        tab.text = resources.getString(R.string.likes)
                    }
                }
            }.attach()
        }
    }

    private fun deleteAccount() {

        deleteDialog()

        repeatOnStarted {
            profileViewModel.deleteResult.collectLatest { result ->
                when (result) {
                    is AccountDelete.Success -> { findNavController().navigate(R.id.action_profileFragment_to_signInFragment) }
                    is AccountDelete.Error -> { binding.profileRootLayout.snackbarError(binding.profileRootLayout, error = result.message, ""){} }
                    is AccountDelete.Loading -> {}
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun deleteDialog(){
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        alertDialogBuilder.apply {
            setTitle(resources.getString(R.string.delete_account_title))
            setMessage(resources.getString(R.string.delete_account_message))
            setPositiveButton(resources.getString(R.string.yes)) { _, _ ->

                val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.custom_dialog, null)
                builder.setView(dialogView)
                val alertDialog = builder.create()

                val cancelBtn = dialogView.findViewById<LinearLayout>(R.id.cancel_btn)
                val deleteBtn = dialogView.findViewById<LinearLayout>(R.id.delete_btn)
                val emailEt = dialogView.findViewById<EditText>(R.id.email_confirm_et)
                val codeEt = dialogView.findViewById<EditText>(R.id.code_sent_from_email_et)
                val emailConfirmBtn = dialogView.findViewById<ImageButton>(R.id.email_confirm_btn)

                emailConfirmBtn.stateDisabled()

                codeEt.deleteDialogCodeWatcher(deleteBtn, codeEt)

                emailEt.deleteDialogEmailWatcher(emailConfirmBtn, emailEt)

                emailConfirmBtn.setOnClickListener { binding.profileRootLayout.snackbar(binding.profileRootLayout, resources.getString(R.string.in_progress)) }

                deleteBtn.stateDisabled()

                cancelBtn.setOnClickListener { alertDialog.dismiss() }

                deleteBtn.setOnClickListener {
                    profileViewModel.deleteUser(SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                    alertDialog.dismiss()
                }
                alertDialog.show()
            }

            setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

            setCancelable(false)
            val alertDialog = create()
            alertDialog.show()
        }
    }

    private fun logoutDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        alertDialogBuilder.apply {
            setTitle(resources.getString(R.string.logout))
            setMessage(resources.getString(R.string.logout_message))

            setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                SessionManager(requireContext()).clearToken()
                stayInViewModel.setUserLoggedIn(false)
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            }

            setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

            setCancelable(true)
            val alertDialog = create()
            alertDialog.show()
        }
    }
}