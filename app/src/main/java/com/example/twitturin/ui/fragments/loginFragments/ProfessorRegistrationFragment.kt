package com.example.twitturin.ui.fragments.loginFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.ui.sealeds.SignUpProfResult
import com.example.twitturin.viewmodel.SignUpViewModel

class ProfessorRegistrationFragment : Fragment() {

    private lateinit var binding : FragmentProfessorRegistrationBinding
    private val editTextList: MutableList<EditText> = mutableListOf()
    private lateinit var viewModel : SignUpViewModel

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
            val birthday = binding.profBirthdayEt.text.toString()
            val password = binding.profPasswordEt.text.toString()
            viewModel.signUpProf(fullName, username, subject, email, birthday, password, "teacher")
        }

        editTextList.add(binding.profFullnameEt)
        editTextList.add(binding.profUsernameEt)
        editTextList.add(binding.profSubjectEt)
        editTextList.add(binding.profEmailEt)
        editTextList.add(binding.profBirthdayEt)
        editTextList.add(binding.profPasswordEt)

        editTextList.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    updateButtonState()
                }
            })
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

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = editTextList.all { editText ->
            editText.text.isNotBlank()
        }
        binding.signUp.isVisible = allFieldsFilled
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfessorRegistrationFragment()
    }
}