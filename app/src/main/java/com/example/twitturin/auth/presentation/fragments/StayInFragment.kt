package com.example.twitturin.auth.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentStayInBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.fragments.FullScreenImageFragment
import com.example.twitturin.profile.sealed.UserCredentials
import com.example.twitturin.profile.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StayInFragment : Fragment() {

    @Inject lateinit var sessionManager : SessionManager
    @Inject lateinit var snackbarHelper : SnackbarHelper
    private lateinit var stayInViewModel: StayInViewModel
    private val profileViewModel : ProfileViewModel by viewModels()
    private val binding by lazy { FragmentStayInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stayInViewModel = ViewModelProvider(requireActivity())[StayInViewModel::class.java]

        val userId = sessionManager.getUserId()

        profileViewModel.getUserCredentials(userId!!)

        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) {result ->
            when(result) {
                is UserCredentials.Success -> {
                    val profileImage = "${result.user.profilePicture ?: R.drawable.person}"
                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .into(binding.stayInProfileImage)
                }

                is UserCredentials.Error -> {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.stayIn_root_layout),
                        requireActivity().findViewById(R.id.stayIn_root_layout),
                        error = result.message,
                        ""
                    ){}
                }
            }
        }

        binding.apply {

            approveBtn.setOnClickListener {
                stayInViewModel.setUserLoggedIn(true)
                findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
            }

            laterBtn.setOnClickListener {
                stayInViewModel.setUserLoggedIn(false)
                findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
            }

            stayInProfileImage.setOnClickListener {
                val fullScreenImageFragment = FullScreenImageFragment()

                stayInProfileImage.buildDrawingCache()
                val originalBitmap = stayInProfileImage.drawingCache
                val image = originalBitmap.copy(originalBitmap.config, true)

                val extras = Bundle()
                extras.putParcelable("image", image)
                fullScreenImageFragment.arguments = extras

                fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.stay_in_gray)
    }

    override fun onPause() {
        super.onPause()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), com.google.android.material.R.color.m3_sys_color_light_surface_container)
    }
}