package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.presentation.sealed.AccountDelete
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var stayInViewModel: StayInViewModel
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this
        val userId = sessionManager.getUserId()
        val baseUserUrl = "https://twitturin.onrender.com/users"

        stayInViewModel = ViewModelProvider(requireActivity())[StayInViewModel::class.java]

        /**this portion of code with viewPager2 added, cause it cause an error: Fragment not found or no longer exist!*/
        binding.vp2.isSaveEnabled = false

        binding.apply {

            shareProfile.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                val link = "$baseUserUrl/$userId"

                intent.putExtra(Intent.EXTRA_TEXT, link)
                intent.type = "text/plain"

                requireContext().startActivity(Intent.createChooser(intent,"Choose app:"))
            }

            followersTv.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_followersListFragment)
            }

            followingTv.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_followingListFragment)
            }

            profileViewModel.getUserCredentials(userId!!)
            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
                profileShimmerLayout.startShimmer()
                when (result) {
                    is UserCredentials.Success -> {
                        profileShimmerLayout.stopShimmer()
                        profileShimmerLayout.visibility = View.GONE
                        val profileImage = "${result.user.profilePicture ?: R.drawable.person}"
                        Glide.with(requireContext())
                            .load(profileImage)
                            .error(R.drawable.not_found)
                            .into(profileUserAvatar)

                        profileFullName.text = result.user.fullName ?: "Twittur User"
                        profileUsername.text = "@" + result.user.username
                        profileKind.text = result.user.kind
                        profileBiography.text = result.user.bio ?: "This user does not appear to have any biography."
                        followingCounterTv.text = result.user.followingCount.toString()
                        followersCounterTv.text = result.user.followersCount.toString()

                        // location
                        if (result.user.country.isNullOrEmpty()) {
                            profileLocationIcon.visibility = View.INVISIBLE
                            profileLocationTv.visibility = View.GONE
                        } else {
                            profileLocationIcon.visibility = View.VISIBLE
                            profileLocationTv.text = result.user.country
                        }

                    }

                    is UserCredentials.Error -> {

                        snackbarHelper.snackbarError(
                            requireActivity().findViewById(R.id.profile_root_layout),
                            requireActivity().findViewById(R.id.profile_root_layout),
                            error = result.message,
                            ""
                        ){}
                    }
                }
            }

            /**
             * This code build to implement listener when user click's on profile image, to open it, in full screen size!
             * */
            profileUserAvatar.setOnClickListener {
                val fullScreenImageFragment = FullScreenImageFragment()

                profileUserAvatar.buildDrawingCache()
                val originalBitmap = profileUserAvatar.drawingCache
                val image = originalBitmap.copy(originalBitmap.config, true)

                val extras = Bundle()
                extras.putParcelable("image", image)
                fullScreenImageFragment.arguments = extras

                fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
            }

            threeDotMenu.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), threeDotMenu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.edit_profile -> {
                            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                            true
                        }

                        R.id.logout -> {
                            logoutDialog()
                            true
                        }

                        R.id.delete_account -> {

                            deleteDialog()
                            profileViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
                                when (result) {

                                    is AccountDelete.Success -> {
                                        findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                    }

                                    is AccountDelete.Error -> {
                                        snackbarHelper.snackbarError(
                                            view.findViewById(R.id.profile_root_layout),
                                            profileRootLayout,
                                            error = result.message,
                                            ""){}
                                    }
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.inflate(R.menu.popup_menu_profile)

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popupMenu)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception){
                    Log.e("Main", "Error showing menu icons.", e)
                } finally {
                    popupMenu.show()
                }
            }

            val adapter = ProfileViewPagerAdapter(childFragmentManager, lifecycle)
            vp2.adapter = adapter
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

            val emailEt = dialogView.findViewById<EditText>(R.id.email_confirm_et)
            val codeEt = dialogView.findViewById<EditText>(R.id.code_sent_from_email_et)
            val cancelBtn = dialogView.findViewById<LinearLayout>(R.id.cancel_btn)
            val deleteBtn = dialogView.findViewById<LinearLayout>(R.id.delete_btn)
            val emailConfirmBtn = dialogView.findViewById<ImageButton>(R.id.email_confirm_btn)

            emailConfirmBtn.isEnabled = false
            val emailTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    emailConfirmBtn.isEnabled = !emailEt.text.isNullOrBlank()
                }

                override fun afterTextChanged(s: Editable?) {
                    //
                }
            }
            emailEt.addTextChangedListener(emailTextWatcher)

            deleteBtn.isEnabled = false
            val codeTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    deleteBtn.isEnabled = !codeEt.text.isNullOrBlank()
                }

                override fun afterTextChanged(s: Editable?) {
                    //
                }
            }
            codeEt.addTextChangedListener(codeTextWatcher)

            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            deleteBtn.setOnClickListener {
                profileViewModel.deleteUser(sessionManager.getUserId()!!, "Bearer ${sessionManager.getToken()}")
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
            sessionManager.clearToken()
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