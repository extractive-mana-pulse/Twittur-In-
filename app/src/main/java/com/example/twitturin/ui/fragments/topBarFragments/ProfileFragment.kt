package com.example.twitturin.ui.fragments.topBarFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.ui.adapters.ProfileViewPagerAdapter
import com.example.twitturin.ui.fragments.FullScreenImageFragment
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this

        val sessionManager = SessionManager(requireContext())
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
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
                        .placeholder(R.drawable.username_person)
                        .into(binding.profileImage)

                    binding.profileName.text = result.user.fullName ?: "Twittur User"

                    binding.customName.text = "@" + result.user.username
                    binding.profileKindTv.text = result.user.kind
                    binding.profileDescription.text = result.user.bio ?: "This user does not appear to have any biography."

                    binding.locationImg.visibility = if (binding.locationTv.text.isEmpty()) {
                        View.GONE
                    } else {
                        binding.locationTv.text = result.user.country
                        View.VISIBLE
                    }

                    binding.emailImg.visibility = if (binding.emailTv.text.isEmpty()) {
                        View.GONE
                    } else {
                        binding.emailTv.text = result.user.country
                        View.VISIBLE
                    }
                    binding.followingCounterTv.text = result.user.followingCount.toString()
                    binding.followersCounterTv.text = result.user.followersCount.toString()

//                    val followingCount = result.user.followingCount

//                    if (followingCount == 0) {
//                        binding.followingTv.text = "No following"
//                        binding.followingCounterTv.visibility = View.INVISIBLE
//                    } else {
//                        binding.followingCounterTv.visibility = View.VISIBLE
//                        binding.followingCounterTv.text = followingCount.toString()
//                    }

//                    val followersCount = result.user.followersCount

//                    if (followersCount == 0) {
//                        binding.followersTv.text = "No followers"
//                        binding.followersCounterTv.visibility = View.INVISIBLE
//                    } else {
//                        binding.followersCounterTv.visibility = View.VISIBLE
//                        binding.followersCounterTv.text = followersCount.toString()
//                    }

                }
                is UserCredentialsResult.Error -> {
                    snackbarError(result.message)
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
                        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
                        alertDialogBuilder.setTitle("Logout")
                        alertDialogBuilder.setMessage("Are you sure you want to log out?")
                        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                            val pref = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.remove("remember")
                            editor.clear()
                            editor.apply()
                            sessionManager.clearToken()
                            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                        }

                        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }

                        alertDialogBuilder.setCancelable(false)
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        true
                    }

                    R.id.delete_account -> {
                        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", MODE_PRIVATE)
                        val username = sharedPreferences.getString("username", "")

                        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
                        alertDialogBuilder.setTitle("${username?.uppercase()}: Are you sure you want to delete account?")
                        alertDialogBuilder.setMessage("Please note that once an account is deleted, it cannot be restored and all activity will be deleted")
                        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                            profileViewModel.deleteUser(userId, "Bearer $token")
                        }

                        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }

                        alertDialogBuilder.setCancelable(true)
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()

                        profileViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is DeleteResult.Success -> {
                                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                    snackbar("Deleted")
                                }
                                is DeleteResult.Error -> {
                                    snackbarError(result.message)
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
                    tab.text = "Tweets"
                }
                1 -> {
                    tab.text = "Likes"
                }
            }
        }.attach()
    }

    fun goBack(){
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun snackbar(message : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.profile_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, message, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_primary))
            .setTextColor(resources.getColor(R.color.md_theme_light_onPrimaryContainer))
        snackbar.show()
    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.profile_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}