package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()
//    private var imagePath by requireContext().sharedPreferences("")
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    private val binding by lazy { FragmentEditProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** TODO
         * Rebuild. When  user navigates from profile to edit profile page. @ sign username.
         * */

        val bio = arguments?.getString("profile_bio")
        val date = arguments?.getString("profile_date")
        val fullname = arguments?.getString("profile_fullname")
        val username = arguments?.getString("profile_username")
//        val userAvatar = arguments?.getByteArray("profile_image")

        binding.apply {

            pickSingleMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    Toast.makeText(requireContext(), "Failed picking media.", Toast.LENGTH_SHORT).show()
                } else {
                    val sharedPreferences = requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("image", it.data?.data.toString()).apply()

                    it.data?.data.toString()

                    Log.d("image", it.data?.data.toString())

                    Glide.with(requireContext())
                        .load(it.data?.data.toString())
                        .into(editProfileUserAvatar)
                }
            }

            val sharedPreferences = requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
            val imagePath = sharedPreferences.getString("image", null)

            Log.d("image", imagePath.toString())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                editProfileUserAvatar.setOnClickListener {
                    pickSingleMediaLauncher.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply { type = "image/*" } )
                    profileViewModel.editUserImage(imagePath!!, SessionManager(requireContext()).getUserId()!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                profileViewModel.editUserImageState.collect{
                    when(it){
                        is ProfileViewModel.EditUserImageState.Error -> {
                            editProfileRootLayout.snackbarError(
                                editProfileRootLayout,
                                error = it.message,
                                actionText = ""
                            ) {}
                        }
                        ProfileViewModel.EditUserImageState.Loading -> {
                            Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                        }
                        is ProfileViewModel.EditUserImageState.Success -> {
                            Snackbar.make(editProfileRootLayout, "Success", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            editProfileBioEt.setText(bio)
            editProfileBirthdayEt.setText(date)
            editProfileFullnameEt.setText(fullname)
            editProfileUsernameEt.setText(username)

            headerLayout.setOnClickListener { /*showColorPickerDialog()*/ }

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
                    is EditUser.Success -> {
                        Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    }

                    is EditUser.Error -> {
                        editProfileRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.edit_profile_root_layout),
                            error = result.error,
                            ""
                        ) {}
                    }
                }
            }
        }
    }
}

//    private fun showDatePickerDialog() {
//        val datePickerDialog = DatePickerDialog(requireActivity(), { _, year, month, day ->
//            calendar.set(year, month, day)
//            updateBirthdayEditText() },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        )
//        datePickerDialog.show()
//    }

//    private fun updateBirthdayEditText() {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val formattedDate = dateFormat.format(calendar.time)
//        binding.birthdayEt.setText(formattedDate).toString()
//    }

//    private fun showColorPickerDialog() {
//
//        val colors = listOf(
//            Color.CYAN,
//            Color.MAGENTA,
//            Color.YELLOW,
//            Color.GREEN,
//            Color.BLUE,
//            Color.RED,
//            Color.rgb(255, 165, 0),
//            Color.rgb(205, 92, 92),
//            Color.rgb(72, 61, 139),
//            Color.rgb(244, 164, 96),
//            Color.rgb(169, 169, 169),
//            Color.rgb(245, 245, 220),
//            Color.rgb(255, 228, 181),
//            Color.rgb(102, 205, 170),
//            Color.rgb(179, 157, 219)
//        )
//
//        val numColumns = 5 // Desired number of columns
//        val padding = dpToPx(15) // Convert 15 dp to pixels
//        val spacing = dpToPx(15) // Set the spacing between items in dp
//
//        val recyclerView = RecyclerView(requireContext()).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            layoutManager = GridLayoutManager(requireActivity(), numColumns)
//            setPadding(padding, dpToPx(20), padding, padding) // Convert padding to pixels
//            adapter = ColorAdapter(requireActivity(), colors) { selectedColor ->
//                binding.headerLayout.setBackgroundColor(selectedColor)
//            }
//            addItemDecoration(GridSpacingItemDecoration(numColumns, spacing, true))
//        }
//
//        val colorPickerDialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
//            .setTitle(resources.getString(R.string.get_color))
//            .setView(recyclerView)
//            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//        colorPickerDialog.show()
//    }

//    private fun dpToPx(dp: Int): Int {
//        return (dp * resources.displayMetrics.density).toInt()
//    }

//    private fun pickPhoto() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri = data?.data
//            // Handle the selected image URI
//        }
//    }
// }