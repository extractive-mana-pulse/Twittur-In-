package com.example.twitturin.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.adapters.ColorAdapter
import com.example.twitturin.ui.decoration.GridSpacingItemDecoration
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.HttpResponses
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager: SessionManager
//    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var binding : FragmentEditProfileBinding

    private val PICK_PHOTO_REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.profileImage.setOnClickListener {
            pickPhoto()
        }

        val token = sessionManager.getToken()
        val userId = sessionManager.getUserId()

        Log.d("token",token.toString())
        Log.d("userId",userId.toString())

        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
//        TODO. this code should be modified under edit text. so in this page in edit text fields should be default value of user credentials
//         or if it's empty field should be empty.

        binding.save.setOnClickListener {

            val fullName = binding.fullnameEt.text.toString()
            val username = binding.usernameEt.text.toString()
            val bio = binding.bioEt.text.toString()
            val email = binding.emailEt.text.toString()
            val country = binding.countryEt.selectedCountryName
            val birthday = binding.birthdayEt.text.toString()

            profileViewModel.editUser(
                fullName,
                username,
                email,
                bio,
                country,
                birthday,
                userId!!,
                "Bearer $token"
            )

            profileViewModel.editUserResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is EditUserResult.Success -> {
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    }

                    is EditUserResult.Error -> {
//                        val x = HttpResponses.Error(statusCode = result.errorMessage)
                        snackbarHelper.snackbarError(
                            view.findViewById<ConstraintLayout>(R.id.edit_profile_root_layout),
                            binding.testTv,
                            error = result.error,
                            ""){}
                    }
                }
            }
        }

        binding.headerLayout.setOnClickListener {
            showColorPickerDialog()
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
            }
            addItemDecoration(GridSpacingItemDecoration(numColumns, spacing, true))
        }

        val colorPickerDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle("Choose a color")
            .setView(recyclerView)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        colorPickerDialog.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            // Handle the selected image URI
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }
}