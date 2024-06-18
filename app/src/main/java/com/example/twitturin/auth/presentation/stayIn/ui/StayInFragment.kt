package com.example.twitturin.auth.presentation.stayIn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

        binding.apply {

            saveBtn.setOnClickListener { stayInViewModel.stayInUiEvent(StayInUiEvent.OnSavePressed) }

            notSaveBtn.setOnClickListener { stayInViewModel.stayInUiEvent(StayInUiEvent.OnNotSavePressed) }

            stayInProfileImage.setOnClickListener { stayInViewModel.stayInUiEvent(StayInUiEvent.FullScreenPressed) }

            profileViewModel.getUserCredentials(sessionManager.getUserId()!!)

            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UserCredentials.Success -> {
                        val profileImage = "${result.user.profilePicture ?: R.drawable.person}"
                        Glide.with(requireContext())
                            .load(profileImage)
                            .error(R.drawable.not_found)
                            .into(stayInProfileImage)
                    }

                    is UserCredentials.Error -> {
                        stayInRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.stayIn_root_layout),
                            error = result.message,
                            ""
                        ) { }
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
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

                        StayInUiEvent.FullScreenPressed -> {
                            val fullScreenImageFragment = FullScreenImageFragment()

                            stayInProfileImage.buildDrawingCache()

                            val extras = Bundle()
                            extras.putParcelable(
                                "image",
                                stayInProfileImage.drawingCache.copy(
                                    stayInProfileImage.drawingCache.config,
                                    true
                                )
                            )
                            fullScreenImageFragment.arguments = extras

                            fullScreenImageFragment.show(
                                requireActivity().supportFragmentManager,
                                "FullScreenImageFragment"
                            )
                        }

                        StayInUiEvent.NothingState -> {}
                    }
                }
            }
        }
    }
}