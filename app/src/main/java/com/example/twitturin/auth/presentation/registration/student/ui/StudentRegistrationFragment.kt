package com.example.twitturin.auth.presentation.registration.student.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.domain.use_case.Username
import com.example.twitturin.auth.presentation.registration.student.sealed.SignUpStudentResult
import com.example.twitturin.auth.presentation.registration.student.sealed.StudRegUiEvent
import com.example.twitturin.auth.presentation.registration.student.vm.StudentRegViewModel
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import com.example.twitturin.databinding.FragmentStudentRegistrationBinding
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentRegistrationFragment : Fragment() {

    private val signUpViewModel : SignUpViewModel by viewModels()
    private val editTextList: MutableList<EditText> = mutableListOf()
    private val studentUiEventViewModel : StudentRegViewModel by viewModels()
    private val binding by lazy { FragmentStudentRegistrationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            studentSignUpBtn.setOnClickListener { studentUiEventViewModel.sentStudRegEvent(StudRegUiEvent.OnRegPressed) }

            editTextList.add(userNameEt)
            editTextList.add(studentIdEt)
            editTextList.add(studentPasswordEt)

            editTextList.forEach { editText ->
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        updateButtonState()
                    }
                })
            }

            ArrayAdapter.createFromResource(requireContext(), R.array.major_array, android.R.layout.simple_spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                planetsSpinner.adapter = adapter
            }

            planetsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {  }

                override fun onNothingSelected(p0: AdapterView<*>?) {  }
            }

            userNameEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //
                }

                override fun afterTextChanged(s: Editable?) {
                    val inputText = s?.toString()

                    if (inputText != null && inputText.contains(" ")) {
                        studentUsernameInputLayout.error = resources.getString(R.string.no_spaces_allowed)
                        studentSignUpBtn.isEnabled = false
                    } else {
                        studentUsernameInputLayout.error = null
                        studentSignUpBtn.isEnabled = true
                    }
                }
            })
        }

        studentUiEventViewModel.studRegEvent.observe(viewLifecycleOwner) {

            when(it) {

                is StudRegUiEvent.OnRegPressed -> {

                    val fullname = binding.fullNameEt.text.toString().trim()
                    val username = binding.userNameEt.text.toString().trim()
                    val studentId = binding.studentIdEt.text.toString().trim()
                    val major = binding.planetsSpinner.selectedItem.toString()
                    val password = binding.studentPasswordEt.text.toString().trim()
                    signUpViewModel.signUpStudent(fullname, username, studentId, major, password, "student")

                    signUpViewModel.signUpStudentResult.observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is SignUpStudentResult.Success -> {
                                findNavController().navigate(R.id.action_studentRegistrationFragment_to_signInFragment)
                            }

                            is SignUpStudentResult.Error -> {
                                binding.studRegRootLayout.snackbarError(
                                    requireActivity().findViewById(R.id.stud_reg_root_layout),
                                    error = result.message,
                                    ""){  }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = editTextList.all { editText ->
            editText.text.isNotEmpty()
        }
        binding.studentSignUpBtn.isVisible = allFieldsFilled
    }
}