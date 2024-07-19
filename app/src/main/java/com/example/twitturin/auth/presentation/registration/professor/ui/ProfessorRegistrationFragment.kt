package com.example.twitturin.auth.presentation.registration.professor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.professor.sealed.ProfRegUiEvent
import com.example.twitturin.auth.presentation.registration.professor.sealed.SignUpProfResult
import com.example.twitturin.core.extensions.listOfEditTexts
import com.example.twitturin.core.extensions.usernameRegistration
import com.example.twitturin.auth.presentation.registration.professor.vm.ProfRegViewModel
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfessorRegistrationFragment : Fragment() {

    private val signUpViewModel  by viewModels<SignUpViewModel>()
    private val professorRegistrationViewModel by viewModels<ProfRegViewModel>()
    private val binding by lazy { FragmentProfessorRegistrationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            signUpProf.setOnClickListener { professorRegistrationViewModel.onRegPressed() }

            professorToolbar.setNavigationOnClickListener { professorRegistrationViewModel.onBackPressed() }

            profUsernameEt.usernameRegistration(profUsernameInputLayout, signUpProf, requireContext())

            listOf(profSubjectEt, profFullnameEt, profUsernameEt, profPasswordEt).listOfEditTexts(signUpProf)

            viewLifecycleOwner.lifecycleScope.launch {
                professorRegistrationViewModel.profRegEvent.collect{
                    when(it){
                        is ProfRegUiEvent.OnAuthPressed -> {
                            val fullName = profFullnameEt.text.toString().trim()
                            val username = profUsernameEt.text.toString().trim()
                            val subject = profSubjectEt.text.toString().trim()
                            val password = profPasswordEt.text.toString().trim()
                            signUpViewModel.signUpProf(fullName, username, subject, password, "teacher")

                            repeatOnStarted {
                                signUpViewModel.profRegResult.collectLatest { result ->
                                    when (result) {
                                        is SignUpProfResult.Success -> {
                                            findNavController().navigate(R.id.action_professorRegistrationFragment_to_signInFragment)
                                        }

                                        is SignUpProfResult.Error -> {
                                            binding.profRegRootLayout.snackbarError(
                                                requireActivity().findViewById(R.id.prof_reg_root_layout),
                                                error = result.message,
                                                ""
                                            ){  }
                                        }
                                        SignUpProfResult.Loading -> root.snackbar(root,R.string.loading.toString())
                                    }
                                }
                            }
                        }
                        ProfRegUiEvent.OnBackPressed -> { findNavController().navigateUp() }
                    }
                }
            }
        }
    }
}