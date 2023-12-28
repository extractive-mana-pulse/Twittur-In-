package com.example.twitturin.ui.fragments.topBarFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.ui.adapters.ProfileViewPagerAdapter
import com.example.twitturin.ui.fragments.FullScreenImageFragment
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.FollowersResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
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

        binding.swipeToRefreshLayout.setOnRefreshListener {
            binding.swipeToRefreshLayout.isRefreshing = false
        }

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

                    val profileImage = "${result.user.profilePicture}"
                    Glide.with(requireContext())
                        .load(profileImage)
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
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
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
                        profileViewModel.deleteUser(userId, "Bearer $token")
                        profileViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is DeleteResult.Success -> {
                                    val pref = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                                    val editor = pref.edit()
                                    editor.remove("remember")
                                    editor.clear()
                                    editor.apply()
                                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                                    Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                                }
                                is DeleteResult.Error -> {
                                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
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

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}