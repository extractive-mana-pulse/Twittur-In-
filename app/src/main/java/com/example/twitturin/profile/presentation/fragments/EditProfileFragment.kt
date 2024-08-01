package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.sharedPreferences
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.toastCustom
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.profile.presentation.adapters.ColorAdapter
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.sealed.EditUserImageState
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.util.GridSpacingItemDecoration
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    private val binding by lazy { FragmentEditProfileBinding.inflate(layoutInflater) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // TODO: Fix navigation. When edit profile done successfully and navigated back to ProfileFragment and after in ProfileFragment press back button. user navigates to EditProfileFragment again!
            headerLayout.setOnClickListener { showColorPickerDialog() }

            editProfileEmailEtLayout.setEndIconOnClickListener { snackbarView.snackbar(snackbarView, resources.getString(R.string.info)) }

            pickSingleMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    requireContext().toastCustom(resources.getString(R.string.failed_to_pick_media))
                } else {
                    val stream = requireActivity().contentResolver.openInputStream(it.data?.data!!)
                    profileViewModel.editUserImage(stream!!, SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")

                    repeatOnStarted {
                        profileViewModel.editUserImageState.collectLatest { result ->
                            when(result) {
                                is EditUserImageState.Success -> { snackbarView.snackbar(snackbarView, resources.getString(R.string.success)) }
                                is EditUserImageState.Error -> { Log.d("image error", result.message) }
                                is EditUserImageState.Loading -> {}
                            }
                        }
                    }
                }
            }

            editProfileUserAvatar.setOnClickListener { pickSingleMediaLauncher.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply { type = "image/*" } ) }

            profileViewModel.getUserCredentials(SessionManager(requireContext()).getUserId()!!)
            repeatOnStarted {
                profileViewModel.getUserCredentials.collectLatest {
                    when(it){
                        is UserCredentials.Error -> { snackbarView.snackbarError(snackbarView, it.message, ""){} }
                        is UserCredentials.Success -> { it.user.apply {
                            editProfileBioEt.setText(bio)
                            editProfileBirthdayEt.setText(birthday)
                            editProfileFullnameEt.setText(fullName)
                            editProfileUsernameEt.setText(username)
                            editProfileUserAvatar.loadImagesWithGlideExt(profilePicture)
                            }
                        }
                        is UserCredentials.Loading -> {}
                    }
                }
            }

            editProfilePageToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            editProfilePageToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save_changes -> {
                        saveChanges()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun saveChanges() {

        binding.apply {

            profileViewModel.editUser(
                editProfileFullnameEt.text.toString(),
                editProfileUsernameEt.text.toString(),
                editProfileEmailEt.text.toString(),
                editProfileBioEt.text.toString(),
                countryEt.selectedCountryName.toString(),
                editProfileBirthdayEt.text.toString(),
                SessionManager(requireContext()).getUserId()!!,
                "Bearer ${SessionManager(requireContext()).getToken()}"
            )

            repeatOnStarted {
                profileViewModel.editUserResult.collectLatest { result ->
                    when (result) {
                        is EditUser.Success -> { findNavController().navigate(R.id.profileFragment) }
                        is EditUser.Error -> { snackbarView.snackbarError(snackbarView, error = result.error, "") {} }
                        is EditUser.Loading -> {}
                    }
                }
            }
        }
    }

    private fun showColorPickerDialog() {

        val colors = listOf(
            Color.TRANSPARENT,
            Color.BLACK,
            Color.DKGRAY,
            Color.GRAY,
            Color.LTGRAY,
            Color.WHITE,
            Color.CYAN,
            Color.MAGENTA,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.RED,
            Color.rgb(255, 165, 0),
            Color.rgb(205, 92, 92),
            Color.rgb(72, 61, 139),
            Color.rgb(244, 164, 96),
            Color.rgb(169, 169, 169),
            Color.rgb(245, 245, 220),
            Color.rgb(255, 228, 181),
            Color.rgb(102, 205, 170),
            Color.rgb(179, 157, 219)
        )

        val numColumns = 5 // Desired number of columns
        val padding = dpToPx(15) // Convert 15 dp to pixels
        val spacing = dpToPx(15) // Set the spacing between items in dp

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = GridLayoutManager(requireActivity(), numColumns)
            setPadding(padding, dpToPx(20), padding, padding) // Convert padding to pixels
            adapter = ColorAdapter(requireActivity(), colors) { selectedColor ->
                binding.headerLayout.setBackgroundColor(selectedColor)

                var color by requireActivity().sharedPreferences("color")
                color = selectedColor.toString()
            }
            addItemDecoration(GridSpacingItemDecoration(numColumns, spacing, true))
        }

        val colorPickerDialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(resources.getString(R.string.get_color))
            .setView(recyclerView)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        colorPickerDialog.show()
    }
    private fun dpToPx(dp: Int): Int =  (dp * resources.displayMetrics.density).toInt()
}