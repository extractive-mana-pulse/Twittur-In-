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
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
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

        profileViewModel.getUserCredentials(userId!!)
        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UserCredentialsResult.Success -> {
                    val profileImage = "${result.user.profilePicture}"

//                    binding.profileImage.setOnClickListener {
//
//                        // TODO when i press to profile image i need to open it in full screen size
//                        val imageUrl = "${result.user.profilePicture}" // Replace with the actual URL of the profile image
//
//                        val action = ProfileFragmentDirections.actionProfileFragmentToFullScreenImageFragment(imageUrl)
//                        findNavController().navigate(action)
//                    }



                    binding.profileImage.setOnClickListener {
                        val fullScreenImageFragment = FullScreenImageFragment()

                        // Create a new copy of the image bitmap
                        binding.profileImage.buildDrawingCache()
                        val originalBitmap = binding.profileImage.drawingCache
                        val image = originalBitmap.copy(originalBitmap.config, true)

                        // Pass the new copy of the bitmap as an argument to the fragment
                        val extras = Bundle()
                        extras.putParcelable("image", image)
                        fullScreenImageFragment.arguments = extras

                        // Start the fragment transaction
                        val fragmentManager = requireActivity().supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_host_fragment_container, fullScreenImageFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }


                    Glide.with(requireContext())
                        .load(profileImage)
                        .into(binding.profileImage)

                    binding.profileName.text = result.user.fullName
                    binding.customName.text = "@" + result.user.username
                    binding.profileKindTv.text = result.user.kind
                    binding.profileDescription.text = result.user.bio
                    binding.locationTv.text = result.user.country
                    binding.emailTv.text = result.user.email
                    binding.followingCounterTv.text = result.user.followingCount.toString()
                    binding.followersCounterTv.text = result.user.followersCount.toString()

                }
                is UserCredentialsResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun ImageView.roundedCornerDrawable(radius: Float) {
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, radius, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        setImageDrawable(BitmapDrawable(resources, outputBitmap))
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}