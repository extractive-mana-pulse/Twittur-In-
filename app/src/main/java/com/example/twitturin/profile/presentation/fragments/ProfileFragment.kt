package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
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
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.ProfileUIEvent
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileUIViewModel
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

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

            val profileImage = profileUserAvatar.drawable?.toBitmap()

            val byteArrayOutputStream = ByteArrayOutputStream()
            profileImage?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val profileImageByteArray = byteArrayOutputStream.toByteArray()

            profileUserAvatar.setOnClickListener { profileUIViewModel.onAvatarPressed() }

            followersTv.setOnClickListener { profileUIViewModel.onFollowersPressed() }

            followingTv.setOnClickListener { profileUIViewModel.onFollowingPressed() }

            profileToolbar.setNavigationOnClickListener { profileUIViewModel.onBackPressed() }

            profileToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share_profile -> {
                        findNavController().navigate(R.id.action_profileFragment_to_shareProfileBottomSheetFragment)
                        true
                    }
                    R.id.three_dot_menu -> {

                        val popupMenu = PopupMenu(requireContext(), threeDotMenuView)

                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {

                                R.id.edit_profile -> {
                                    val bundle = Bundle().apply {
                                        putString("profile_fullname", profileFullName.text.toString())
                                        putString("profile_username", profileUsername.text.toString())
                                        putString("profile_bio", profileBiography.text.toString())
                                        putString("profile_date", profileDateTv.text.toString())
                                        putByteArray("profile_image", profileImageByteArray)
                                    }
                                    findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
                                    true
                                }

                                R.id.logout -> {
                                    logoutDialog()
                                    true
                                }

                                R.id.delete_account -> {
                                    deleteAccount()
                                    true
                                }
                                else -> false
                            }
                        }

                        popupMenu.inflate(R.menu.popup_menu_profile)

                        popupMenu.converter(popupMenu)
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

            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->

                profileShimmerLayout.startShimmer()
                when (result) {
                    is UserCredentials.Success -> {

                        profileShimmerLayout.stopShimmer()
                        profileShimmerLayout.visibility = View.GONE

                        result.apply {

                            profileKind.text = user.kind
                            profileDateTv.text = user.studentId
                            profileUsername.text = user.username
                            followingCounterTv.text = user.followingCount.toString()
                            followersCounterTv.text = user.followersCount.toString()
                            profileUserAvatar.loadImagesWithGlideExt(user.profilePicture)
                            profileFullName.text = (user.fullName ?: R.string.default_user_fullname).toString()
                            profileBiography.text = (user.bio ?: R.string.empty_bio).toString()

                            // location
                            if (user.country.isNullOrEmpty()) {
                                profileLocationIcon.beGone()
                                profileLocationTv.beGone()
                            } else {
                                profileLocationIcon.beVisible()
                                profileLocationTv.beVisible()
                            }
                        }
                    }

                    is UserCredentials.Error -> { binding.profileRootLayout.snackbarError(profileRootLayout, error = result.message, ""){} }
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

                    is AccountDelete.Loading -> {  }
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun deleteDialog(){
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        alertDialogBuilder.setTitle(resources.getString(R.string.delete_account_title))
        alertDialogBuilder.setMessage(resources.getString(R.string.delete_account_message))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->

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

            emailConfirmBtn.isEnabled = false

            codeEt.deleteDialogCodeWatcher(deleteBtn, codeEt)

            emailEt.deleteDialogEmailWatcher(emailConfirmBtn, emailEt)

            emailConfirmBtn.setOnClickListener { Snackbar.make(binding.profileRootLayout, R.string.in_progress,Snackbar.LENGTH_SHORT).show() }

            deleteBtn.isEnabled = false

            cancelBtn.setOnClickListener { alertDialog.dismiss() }

            deleteBtn.setOnClickListener {
                profileViewModel.deleteUser(SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialogBuilder.setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun logoutDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        alertDialogBuilder.setTitle(resources.getString(R.string.logout))
        alertDialogBuilder.setMessage(resources.getString(R.string.logout_message))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            SessionManager(requireContext()).clearToken()
            stayInViewModel.setUserLoggedIn(false)
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialogBuilder.setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}