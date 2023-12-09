package com.example.twitturin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentEditProfileBinding
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.viewmodel.ProfileViewModel

class EditProfileFragment : Fragment() {

    private lateinit var binding : FragmentEditProfileBinding

    private val editUserViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val sessionManager = SessionManager(requireContext())

        val token = sessionManager.getToken()
        val userId = sessionManager.getUserId()

        binding.save.setOnClickListener {
            if (!token.isNullOrEmpty()){
                val fullName = binding.fullnameEt.text.toString()
                val username = binding.fullnameEt.text.toString()
                val bio = binding.bioEt.text.toString()
                val country = binding.countryEt.text.toString()
                val birthday = binding.birthdayEt.text.toString()
                editUserViewModel.editUser(fullName, username, bio, country, birthday, userId!!, token)
            } else {
                Toast.makeText(requireContext(), "something went wrong my G", Toast.LENGTH_SHORT).show()
            }


            editUserViewModel.editUserResult.observe(viewLifecycleOwner) { result ->
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

//        binding.save.setOnClickListener {
//            editUserViewModel.editUserResult.observe(viewLifecycleOwner) { result ->
//                when (result) {
//                    is EditUserResult.Loading -> {
//                        // Show loading state
//                    }
//
//                    is EditUserResult.Success -> {
//                        // do something
//                    }
//
//                    is EditUserResult.Error -> {
//                        // Show error message
//                        val errorMessage = result.errorMessage
//                    }
//                }
//            }
//        }

        binding.HeaderLayout.setOnClickListener {
//            TODO { open camera or gallery }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }
}