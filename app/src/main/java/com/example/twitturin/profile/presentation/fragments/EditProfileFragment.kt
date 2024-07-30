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
import android.widget.Toast
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
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.profile.presentation.adapters.ColorAdapter
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.sealed.EditUserImageState
import com.example.twitturin.profile.presentation.util.GridSpacingItemDecoration
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    private val binding by lazy { FragmentEditProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Rework this page.

//        var imagePath by requireActivity().sharedPreferences("imagePath")

        val bio = arguments?.getString("profile_bio")
        val date = arguments?.getString("profile_date")
        val avatar = arguments?.getByteArray("profile_image")
        val username = arguments?.getString("profile_username")
        val fullname = arguments?.getString("profile_fullname")

        binding.apply {

            headerLayout.setOnClickListener { showColorPickerDialog() }

            editProfileEmailEtLayout.setEndIconOnClickListener { snackarView.snackbar(snackarView, resources.getString(R.string.info)) }

            pickSingleMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    Toast.makeText(requireContext(), "Failed picking media.", Toast.LENGTH_SHORT).show()
                } else {
                    val stream = requireActivity().contentResolver.openInputStream(it.data?.data!!)
                    profileViewModel.editUserImage(stream!!, SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")

                    repeatOnStarted {
                        profileViewModel.editUserImageState.collectLatest { result ->

                            when(result) {
                                is EditUserImageState.Success -> { editProfileRootLayout.snackbar(editProfileRootLayout, R.string.success.toString()) }
                                is EditUserImageState.Error -> { Log.d("error", result.message) }
                                is EditUserImageState.Loading -> {}
                            }
                        }
                    }
                }
            }

            editProfileUserAvatar.setOnClickListener { pickSingleMediaLauncher.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply { type = "image/*" } ) }

            editProfileBioEt.setText(bio)
            editProfileBirthdayEt.setText(date)
            editProfileFullnameEt.setText(fullname)
            editProfileUsernameEt.setText(username)

            editProfilePageToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            editProfileUserAvatar.loadImagesWithGlideExt(avatar.toString())

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

            val bio = editProfileBioEt.text.toString()
            val country = countryEt.selectedCountryName
            val email = editProfileEmailEt.text.toString()
            val fullName = editProfileFullnameEt.text.toString()
            val username = editProfileUsernameEt.text.toString()
            val birthday = editProfileBirthdayEt.text.toString()

            profileViewModel.editUser(
                fullName,
                username,
                email,
                bio,
                country.toString(),
                birthday,
                SessionManager(requireContext()).getUserId()!!,
                "Bearer ${SessionManager(requireContext()).getToken()}"
            )

            profileViewModel.editUserResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is EditUser.Success -> { findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment) }

                    is EditUser.Error -> { snackarView.snackbarError(snackarView, error = result.error, "") {} }
                }
            }
        }
    }

    private fun showColorPickerDialog() {

        val colors = listOf(
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