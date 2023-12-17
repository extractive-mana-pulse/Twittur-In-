package com.example.twitturin.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.GridSpacingItemDecoration
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.ui.adapters.ColorAdapter
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var binding : FragmentEditProfileBinding
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()
        val userId = sessionManager.getUserId()

        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
//        TODO. this code should be modified under edit text. so in this page in edit text fields should be default value of user credentials
//         or if it's empty field should be empty.
//        profileViewModel.getUserCredentials(userId!!)
//        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
//            when (result) {
//
//                is UserCredentialsResult.Success -> {
//
//                    binding.fullnameEt.text = result.user.fullName
//                    binding.customName.text = "@" + result.user.username
//                    binding.profileKindTv.text = result.user.kind
//                    binding.profileDescription.text = result.user.bio
//                    binding.locationTv.text = result.user.country
//                    binding.emailTv.text = result.user.email
//
//                }
//                is UserCredentialsResult.Error -> {
//                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        binding.birthdayEt.setOnClickListener {
            Log.d("birthday edit text test" ,"Clicked ?")
            showDatePickerDialog()
        }

        binding.save.setOnClickListener {

            if (!token.isNullOrEmpty()){

                val fullName = binding.fullnameEt.text.toString()
                val username = binding.usernameEt.text.toString()
                val bio = binding.bioEt.text.toString()
                val email = binding.emailEt.text.toString()
                val country = binding.countryEt.selectedCountryName

                profileViewModel.editUser(fullName, username, email, bio, country, updateBirthdayEditText().toString(), userId!!, token)
            } else {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
            }


            profileViewModel.editUserResult.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is EditUserResult.Success -> {
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    }

                    is EditUserResult.Error -> {
                        Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.headerLayout.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(requireActivity(), { _, year, month, day ->
            calendar.set(year, month, day)
            updateBirthdayEditText() },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateBirthdayEditText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.birthdayEt.setText(formattedDate).toString()
    }

    /** DVRST **/
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
                // Do something with the selected color
                // Change Background Color
                binding.headerLayout.setBackgroundColor(selectedColor)
//                selectedColor.dismiss()
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

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }
}