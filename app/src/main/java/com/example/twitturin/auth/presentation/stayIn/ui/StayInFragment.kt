package com.example.twitturin.auth.presentation.stayIn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.sealed.StayInUiEvent
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentStayInBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.fragments.FullScreenImageFragment
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StayInFragment : Fragment() {

    @Inject lateinit var sessionManager : SessionManager
    private val stayInViewModel: StayInViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private val binding by lazy { FragmentStayInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stayInFragment = this

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
                    binding.stayInRootLayout.snackbarError(
                        requireActivity().findViewById(R.id.stayIn_root_layout),
                        error = result.message,
                        ""){  }
                }
            }
        }

        stayInViewModel.stayInEvent.observe(viewLifecycleOwner){
            when(it){
                is StayInUiEvent.OnSavePressed -> {
                    stayInViewModel.setUserLoggedIn(true)
                    findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                }
                is StayInUiEvent.OnNotSavePressed -> {
                    stayInViewModel.setUserLoggedIn(false)
                    findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                }
            }
        }

        binding.apply {

            saveBtn.setOnClickListener {
                stayInViewModel.stayInUiEvent(StayInUiEvent.OnSavePressed)
            }

            notSaveBtn.setOnClickListener {
                stayInViewModel.stayInUiEvent(StayInUiEvent.OnNotSavePressed)
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
}