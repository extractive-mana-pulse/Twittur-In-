package com.example.twitturin.profile.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.profile.presentation.screens.EditProfileScreen
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var pickMediaLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    requireActivity().contentResolver.openInputStream(uri)?.let { stream ->
                        val session = SessionManager(requireContext())
                        profileViewModel.editUserImage(
                            stream,
                            session.getUserId().orEmpty(),
                            "Bearer ${session.getToken()}"
                        )
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val session = SessionManager(requireContext())
        val userId = session.getUserId().orEmpty()
        val token = "Bearer ${session.getToken()}"
        profileViewModel.getUserCredentials(userId)

        return ComposeView(requireContext()).apply {
            setContent {
                EditProfileScreen(
                    viewModel = profileViewModel,
                    onBack = { findNavController().navigateUp() },
                    onPickImage = {
                        pickMediaLauncher.launch(
                            Intent(MediaStore.ACTION_PICK_IMAGES).apply { type = "image/*" }
                        )
                    },
                    onSave = { fullName, username, email, bio, country, birthday ->
                        profileViewModel.editUser(fullName, username, email, bio, country, birthday, userId, token)
                    },
                    onSaved = { findNavController().navigate(R.id.profileFragment) }
                )
            }
        }
    }
}
