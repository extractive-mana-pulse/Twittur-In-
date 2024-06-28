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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.professor.util.usernameRegistration
import com.example.twitturin.auth.presentation.registration.student.sealed.SignUpStudentResult
import com.example.twitturin.auth.presentation.registration.student.sealed.StudentRegistrationUiEvent
import com.example.twitturin.auth.presentation.registration.student.vm.StudentRegistrationViewModel
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import com.example.twitturin.databinding.FragmentStudentRegistrationBinding
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentRegistrationFragment : Fragment() {

    private val signUpViewModel : SignUpViewModel by viewModels()
    private val editTextList: MutableList<EditText> = mutableListOf()
    private val studentUiEventViewModel : StudentRegistrationViewModel by viewModels()
    private val binding by lazy { FragmentStudentRegistrationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            backBtnStudent.setOnClickListener { studentUiEventViewModel.onStudentBackPressed() }

            studentSignUpBtn.setOnClickListener { studentUiEventViewModel.onStudentRegistrationClicked() }

            userNameEt.usernameRegistration(studentUsernameInputLayout, studentSignUpBtn, requireContext())

            viewLifecycleOwner.lifecycleScope.launch {
                studentUiEventViewModel.validationEvents.collect{
                    when(it) {

                        StudentRegistrationUiEvent.OnRegisterPressed -> {
                            val fullname = fullNameEt.text.toString().trim()
                            val username = userNameEt.text.toString().trim()
                            val studentId = studentIdEt.text.toString().trim()
                            val major = majorSpinner.selectedItem.toString()
                            val password = studentPasswordEt.text.toString().trim()

                            signUpViewModel.signUpStudent(fullname, username, studentId, major, password, "student")

                            signUpViewModel.signUpStudentResult.observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is SignUpStudentResult.Success -> {
                                        findNavController().navigate(R.id.action_studentRegistrationFragment_to_signInFragment)
                                    }

                                    is SignUpStudentResult.Error -> {
                                        studRegRootLayout.snackbarError(
                                            requireActivity().findViewById(R.id.stud_reg_root_layout),
                                            error = result.message,
                                            ""){  }
                                    }
                                }
                            }
                        }

                        StudentRegistrationUiEvent.OnBackPressed ->  { findNavController().navigateUp() }

                        StudentRegistrationUiEvent.NothingState -> {  }
                    }
                }
            }

            ArrayAdapter.createFromResource(requireContext(), R.array.major_array, android.R.layout.simple_spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                majorSpinner.adapter = adapter
            }

            majorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {  }

                override fun onNothingSelected(p0: AdapterView<*>?) {  }
            }

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
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = editTextList.all { editText ->
            editText.text.isNotEmpty()
        }
        binding.studentSignUpBtn.isVisible = allFieldsFilled
    }
}