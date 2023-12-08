package com.example.twitturin.ui.fragments.loginFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.ui.sealeds.SignUpProfResult
import com.example.twitturin.viewmodel.SignUpViewModel

class ProfessorRegistrationFragment : Fragment() {

    private lateinit var binding : FragmentProfessorRegistrationBinding

    private lateinit var viewModel : SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfessorRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]

        binding.signUp.setOnClickListener {

            val fullName = binding.profFullnameEt.text.toString()
            val username = binding.profUsernameEt.text.toString()
            val subject = binding.profSubjectEt.text.toString()
            val email = binding.profEmailEt.text.toString()
            val password = binding.profPasswordEt.text.toString()

            viewModel.signUpProf(fullName, username, subject, email, password, "teacher")
        }

        viewModel.profRegResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignUpProfResult.Success -> {
                    findNavController().navigate(R.id.action_professorRegistrationFragment_to_signInFragment)
                }

                is SignUpProfResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfessorRegistrationFragment()
    }
}