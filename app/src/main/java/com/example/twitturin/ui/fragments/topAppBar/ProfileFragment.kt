package com.example.twitturin.ui.fragments.topAppBar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.adapters.ProfileViewPagerAdapter
import com.example.twitturin.ui.fragments.FullScreenImageFragment
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this


        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val userId = sessionManager.getUserId()
        val token = sessionManager.getToken()


        binding.followersTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followersListFragment)
        }

        binding.followingTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followingListFragment)
        }

        profileViewModel.getUserCredentials(userId!!)
        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UserCredentialsResult.Success -> {

                    val profileImage = "${result.user.profilePicture ?: R.drawable.username_person}"
                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .into(binding.profileImage)

                    binding.profileName.text = result.user.fullName ?: "Twittur User"

                    binding.customName.text = "@" + result.user.username
                    binding.profileKindTv.text = result.user.kind
                    binding.profileDescription.text = result.user.bio ?: "This user does not appear to have any biography."

                    if (binding.locationTv.text.isEmpty()) {
                        binding.locationImg.visibility = View.INVISIBLE
                        binding.locationTv.visibility = View.INVISIBLE
                    } else {
                        binding.locationImg.visibility = View.VISIBLE
                        binding.locationTv.visibility = View.VISIBLE
                    }

//                    binding.locationImg.visibility = if (binding.locationTv.text.isEmpty()) {
//                        View.GONE
//                    } else {
//                        binding.locationTv.text = result.user.country
//                        View.VISIBLE
//                    }

                    binding.emailImg.visibility = if (binding.emailTv.text.isEmpty()) {
                        View.GONE
                    } else {
                        binding.emailTv.text = result.user.country
                        View.VISIBLE
                    }
                    binding.followingCounterTv.text = result.user.followingCount.toString()
                    binding.followersCounterTv.text = result.user.followersCount.toString()
                }

                is UserCredentialsResult.Error -> {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.profile_root_layout),
                        requireActivity().findViewById(R.id.profile_root_layout),
                        error = result.message,
                        ""){}
                }
            }
        }

        binding.profileImage.setOnClickListener {
            val fullScreenImageFragment = FullScreenImageFragment()

            binding.profileImage.buildDrawingCache()
            val originalBitmap = binding.profileImage.drawingCache
            val image = originalBitmap.copy(originalBitmap.config, true)

            val extras = Bundle()
            extras.putParcelable("image", image)
            fullScreenImageFragment.arguments = extras

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_container, fullScreenImageFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
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

                                is DeleteResult.Success -> {
                                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                }

                                is DeleteResult.Error -> {
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

    private fun deleteDialog(){
        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
        alertDialogBuilder.setTitle("${username?.uppercase()}: + ${resources.getString(R.string.delete_title)}")
        alertDialogBuilder.setMessage(resources.getString(R.string.delete_message))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            profileViewModel.deleteUser(sessionManager.getUserId()!!, "Bearer ${sessionManager.getToken()}")
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

    fun goBack() {
        findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}