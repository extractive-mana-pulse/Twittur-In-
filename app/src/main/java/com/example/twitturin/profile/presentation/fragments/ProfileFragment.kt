package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.profile.sealed.AccountDelete
import com.example.twitturin.profile.sealed.UserCredentials
import com.example.twitturin.profile.vm.ProfileViewModel
import com.example.twitturin.ui.fragments.FullScreenImageFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
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

        binding.followersTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followersListFragment)
        }

        binding.followingTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followingListFragment)
        }

        profileViewModel.getUserCredentials(userId!!)
        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
            binding.profileShimmerLayout.startShimmer()
            when (result) {
                is UserCredentials.Success -> {
                    binding.profileShimmerLayout.stopShimmer()
                    binding.profileShimmerLayout.visibility = View.GONE
                    val profileImage = "${result.user.profilePicture ?: R.drawable.username_person}"
                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .into(binding.profileImage)

                    binding.profileFullName.text = result.user.fullName ?: "Twittur User"
                    binding.profileUsername.text = "@" + result.user.username
                    binding.profileKindTv.text = result.user.kind
                    binding.profileBiography.text = result.user.bio ?: "This user does not appear to have any biography."
                    binding.followingCounterTv.text = result.user.followingCount.toString()
                    binding.followersCounterTv.text = result.user.followersCount.toString()

                    // location
                    if (result.user.country.isNullOrEmpty()) {
                        binding.profileLocationImg.visibility = View.INVISIBLE
                        binding.profileLocationTv.visibility = View.GONE
                    } else {
                        binding.profileLocationImg.visibility = View.VISIBLE
                        binding.profileLocationTv.text = result.user.country
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
         * This code build to implement listener when user press on profile image open it in full screen size!
         * */
        binding.profileImage.setOnClickListener {
            val fullScreenImageFragment = FullScreenImageFragment()

            binding.profileImage.buildDrawingCache()
            val originalBitmap = binding.profileImage.drawingCache
            val image = originalBitmap.copy(originalBitmap.config, true)

            val extras = Bundle()
            extras.putParcelable("image", image)
            fullScreenImageFragment.arguments = extras

            fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
        }



        binding.threeDotMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.threeDotMenu)

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
                                        binding.profileRootLayout,
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
        binding.vp2.adapter = adapter
        TabLayoutMediator(binding.tb, binding.vp2) { tab, pos ->
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
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialogBuilder.setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}