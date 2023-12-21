package com.example.twitturin.ui.fragments.loginFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentStudentRegistrationBinding
import com.example.twitturin.ui.sealeds.SignUpStudentResult
import com.example.twitturin.viewmodel.SignUpViewModel

class StudentRegistrationFragment : Fragment() {

    private lateinit var binding : FragmentStudentRegistrationBinding
    private val editTextList: MutableList<EditText> = mutableListOf()

    private lateinit var viewModel : SignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStudentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.signUp.setOnClickListener {
            val username = binding.userNameEt.text.toString().trim()
            val studentId = binding.studentIdEt.text.toString().trim()
            val major = binding.majorEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            viewModel.signUp(username, studentId, major, password, "student")
        }

        editTextList.add(binding.userNameEt)
        editTextList.add(binding.studentIdEt)
        editTextList.add(binding.majorEt)
        editTextList.add(binding.passwordEt)

        // Set text change listeners for each EditText
        editTextList.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    updateButtonState()
                }
            })
        }

        viewModel.signUpStudentResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignUpStudentResult.Success -> {
                    findNavController().navigate(R.id.action_studentRegistrationFragment_to_signInFragment)
                    Toast.makeText(requireContext(), "Success?", Toast.LENGTH_SHORT).show()
                }

                is SignUpStudentResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backBtnStudent.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = editTextList.all { editText ->
            editText.text.isNotEmpty()
        }
        binding.signUp.isVisible = allFieldsFilled
    }

    companion object {
        @JvmStatic
        fun newInstance() = StudentRegistrationFragment()
    }
}