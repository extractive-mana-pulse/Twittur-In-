package com.example.twitturin.auth.presentation.registration.student.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.student.sealed.SignUpStudentResult
import com.example.twitturin.auth.presentation.registration.student.sealed.StudentRegistrationUiEvent
import com.example.twitturin.auth.presentation.registration.student.vm.StudentRegistrationViewModel
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import com.example.twitturin.core.extensions.listOfEditTexts
import com.example.twitturin.core.extensions.populateFromResource
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.usernameRegistration
import com.example.twitturin.databinding.FragmentStudentRegistrationBinding
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentRegistrationFragment : Fragment() {

    private val signUpViewModel by viewModels<SignUpViewModel>()
    private val studentUiEventViewModel by viewModels<StudentRegistrationViewModel>()
    private val binding by lazy { FragmentStudentRegistrationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            listOf(userNameEt, studentIdEt, studentPasswordEt).listOfEditTexts(studentSignUpBtn)

            studentToolbar.setOnClickListener { studentUiEventViewModel.onStudentBackPressed() }

            studentSignUpBtn.setOnClickListener { studentUiEventViewModel.onStudentRegistrationClicked() }

            userNameEt.usernameRegistration(studentUsernameInputLayout, studentSignUpBtn, requireContext())

            viewLifecycleOwner.lifecycleScope.launch {
                studentUiEventViewModel.validationEvents.collect {
                    when (it) {

                        StudentRegistrationUiEvent.OnRegisterPressed -> {

                            val fullname = fullNameEt.text.toString().trim()
                            val username = userNameEt.text.toString().trim()
                            val studentId = studentIdEt.text.toString().trim()
                            val major = majorSpinner.selectedItem.toString()
                            val password = studentPasswordEt.text.toString().trim()

                            signUpViewModel.signUpStudent(fullname, username, studentId, major, password, "student")

                            repeatOnStarted {
                                signUpViewModel.signUpStudentResult.collectLatest { result ->
                                    when (result) {
                                        is SignUpStudentResult.Success -> {
                                            findNavController().navigate(R.id.action_studentRegistrationFragment_to_signInFragment)
                                        }

                                        is SignUpStudentResult.Error -> {
                                            studRegRootLayout.snackbarError(
                                                studRegRootLayout,
                                                error = result.message,
                                                ""
                                            ) { }
                                        }

                                        SignUpStudentResult.Loading -> studRegRootLayout.snackbar(
                                            studRegRootLayout,
                                            R.string.loading.toString()
                                        )
                                    }
                                }
                            }
                        }
                        StudentRegistrationUiEvent.OnBackPressed -> {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
            majorSpinner.populateFromResource(requireContext(), R.array.major_array, android.R.layout.simple_spinner_item)
        }
    }
}