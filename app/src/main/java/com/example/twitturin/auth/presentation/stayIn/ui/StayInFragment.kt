package com.example.twitturin.auth.presentation.stayIn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.sealed.StayInUiEvent
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.fullScreenImage
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentStayInBinding
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StayInFragment : Fragment() {

    private val stayInViewModel: StayInViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private val binding by lazy { FragmentStayInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stayInFragment = this
        binding.apply {

            saveBtn.setOnClickListener { stayInViewModel.onSavePressed() }
            notSaveBtn.setOnClickListener { stayInViewModel.onNotSavePressed() }
            stayInProfileImage.setOnClickListener { stayInViewModel.onFullScreenPressed() }

            profileViewModel.getUserCredentials(SessionManager(requireContext()).getUserId()!!)
            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UserCredentials.Success -> { stayInProfileImage.loadImagesWithGlideExt(result.user.profilePicture) }

                    is UserCredentials.Error -> { stayInRootLayout.snackbarError(stayInRootLayout, error = result.message, "") { } }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                stayInViewModel.stayInEvent.collect {
                    when (it) {

                        is StayInUiEvent.OnSavePressed -> {
                            stayInViewModel.setUserLoggedIn(true)
                            findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                        }

                        is StayInUiEvent.OnNotSavePressed -> {
                            stayInViewModel.setUserLoggedIn(false)
                            findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                        }

                        StayInUiEvent.FullScreenPressed -> { fullScreenImage(stayInProfileImage) }
                    }
                }
            }
        }
    }
}