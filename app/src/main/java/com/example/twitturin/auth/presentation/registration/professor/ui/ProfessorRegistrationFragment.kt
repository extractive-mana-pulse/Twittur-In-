package com.example.twitturin.auth.presentation.registration.professor.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.professor.sealed.ProfRegUiEvent
import com.example.twitturin.auth.presentation.registration.professor.sealed.SignUpProfResult
import com.example.twitturin.auth.presentation.registration.professor.util.addAutoResizeTextWatcherOfProf
import com.example.twitturin.auth.presentation.registration.professor.vm.ProfRegViewModel
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfessorRegistrationFragment : Fragment() {

    private val signUpViewModel : SignUpViewModel by viewModels()
    private val profEditTextList: MutableList<EditText> = mutableListOf()
    private val professorRegistrationViewModel : ProfRegViewModel by viewModels()
    private val binding by lazy { FragmentProfessorRegistrationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.professorFragment = this

        binding.apply {

            signUpProf.setOnClickListener { professorRegistrationViewModel.sentProfRegEvent(ProfRegUiEvent.OnAuthPressed) }

            profUsernameEt.addAutoResizeTextWatcherOfProf(profUsernameInputLayout, signUpProf)

            professorRegistrationViewModel.profRegEvent.observe(viewLifecycleOwner){
                when(it){
                    is ProfRegUiEvent.OnAuthPressed -> {
                        val fullName = profFullnameEt.text.toString().trim()
                        val username = profUsernameEt.text.toString().trim()
                        val subject = profSubjectEt.text.toString().trim()
                        val password = profPasswordEt.text.toString().trim()
                        signUpViewModel.signUpProf(fullName, username, subject, password, "teacher")

                        signUpViewModel.profRegResult.observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is SignUpProfResult.Success -> {
                                    findNavController().navigate(R.id.action_professorRegistrationFragment_to_signInFragment)
                                }

                                is SignUpProfResult.Error -> {
                                    binding.profRegRootLayout.snackbarError(
                                        requireActivity().findViewById(R.id.prof_reg_root_layout),
                                        error = result.message,
                                        ""
                                    ){ /*Action CallBack*/ }
                                }
                            }
                        }
                    }
                }
            }

            profEditTextList.add(binding.profFullnameEt)
            profEditTextList.add(binding.profUsernameEt)
            profEditTextList.add(binding.profSubjectEt)
            profEditTextList.add(binding.profPasswordEt)

            profEditTextList.forEach { editText ->
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
                        val allFieldsFilled = profEditTextList.all { editText ->
                            editText.text.isNotBlank()
                        }
                        signUpProf.isVisible = allFieldsFilled
                    }
                })
            }
        }
    }
}