package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.deleteDialogCodeWatcher
import com.example.twitturin.core.extensions.deleteDialogEmailWatcher
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.ProfileUIEvent
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.core.extensions.converter
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.profile.presentation.vm.ProfileUIViewModel
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val stayInViewModel: StayInViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val profileUIViewModel: ProfileUIViewModel by viewModels()
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

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


            /**
             * This code build to implement listener when user click's on profile image, to open it, in full screen size!
             * */
            profileUserAvatar.setOnClickListener { profileUIViewModel.onAvatarPressed() }

            followersTv.setOnClickListener { profileUIViewModel.onFollowersPressed() }

            followingTv.setOnClickListener { profileUIViewModel.onFollowingPressed() }

            profileToolbar.setNavigationOnClickListener { profileUIViewModel.onBackPressed() }

            profileToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share_profile -> {
                        shareProfile()
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
                        ProfileUIEvent.OnAvatarPressed -> { fullScreenAvatar() }
                        ProfileUIEvent.OnBackPressed -> { findNavController().navigateUp() }
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


                        Glide.with(requireContext())
                            .load("${result.user.profilePicture ?: R.drawable.person}")
                            .error(R.drawable.not_found)
                            .into(profileUserAvatar)

                        result.apply {

                            profileKind.text = user.kind
                            profileDateTv.text = user.birthday
                            profileUsername.text = user.username
                            followingCounterTv.text = user.followingCount.toString()
                            followersCounterTv.text = user.followersCount.toString()
                            profileFullName.text = (user.fullName ?: R.string.default_user_fullname).toString()
                            profileBiography.text = (user.bio ?: R.string.empty_bio).toString()

                            // location
                            if (user.country.isNullOrEmpty()) {
                                profileLocationIcon.visibility = View.INVISIBLE
                                profileLocationTv.visibility = View.GONE
                            } else {
                                profileLocationIcon.visibility = View.VISIBLE
                                profileLocationTv.text = user.country
                            }
                        }
                    }

                    is UserCredentials.Error -> {
                        binding.profileRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.profile_root_layout),
                            error = result.message,
                            ""){}
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

    private fun shareProfile() {
        val userId = SessionManager(requireContext()).getUserId()
        val baseUserUrl = "https://twitturin.onrender.com/users"
        val intent = Intent(Intent.ACTION_SEND)
        val link = "$baseUserUrl/$userId"

        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.type = "text/plain"

        requireContext().startActivity(Intent.createChooser(intent,"Choose app:"))
    }

    private fun fullScreenAvatar() {
        val fullScreenImageFragment = FullScreenImageFragment()

        binding.profileUserAvatar.buildDrawingCache()
        val originalBitmap = binding.profileUserAvatar.drawingCache
        val image = originalBitmap.copy(originalBitmap.config, true)

        val extras = Bundle()
        extras.putParcelable("image", image)
        fullScreenImageFragment.arguments = extras

        fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
    }

    private fun deleteAccount() {

        deleteDialog()

        profileViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {

                is AccountDelete.Success -> {
                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                }

                is AccountDelete.Error -> {
                    binding.profileRootLayout.snackbarError(
                        requireActivity().findViewById(R.id.profile_root_layout),
                        error = result.message,
                        ""){}
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