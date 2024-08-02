package com.example.twitturin.auth.presentation.login.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.login.sealed.SignIn
import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent
import com.example.twitturin.auth.presentation.login.vm.SignInUiEventViewModel
import com.example.twitturin.auth.presentation.login.vm.SignInViewModel
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.extensions.login
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.retry
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val signInViewModel by viewModels<SignInViewModel>()
    private val stayInViewModel by viewModels<StayInViewModel>()
    private val signInUiEventViewModel by viewModels<SignInUiEventViewModel>()
    private val binding by lazy { FragmentSignInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("ResourceAsColor", "RestrictedApi", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        binding.apply {
            passwordEt.login(usernameSignInEt, passwordEt, signIn)
            usernameSignInEt.login(usernameSignInEt, passwordEt, signIn)

            signIn.setOnClickListener { signInUiEventViewModel.onLoginPressed() }
            signUpTv.setOnClickListener { signInUiEventViewModel.onKindPressed() }

            /** code hide inputted password immediately */
//            passwordEt.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
//            passwordEt.transformationMethod = PasswordTransformationMethod()
        }

        repeatOnStarted {
            signInUiEventViewModel.signInEvent.collect { event ->
                when (event) {
                    is SignInUiEvent.OnLoginPressed -> {
                        val username = binding.usernameSignInEt.text.toString().trim()
                        val password = binding.passwordEt.text.toString().trim()
                        signInViewModel.signIn(username, password)
                    }
                    is SignInUiEvent.OnKindPressed -> { findNavController().navigate(R.id.action_signInFragment_to_kindFragment) }
                    is SignInUiEvent.StateNoting -> {}
                }
            }
        }

        repeatOnStarted {
            signInViewModel.signInResponse.collect { result ->
                when (result) {
                    is SignIn.Success -> {

                        val token = signInViewModel.token.value
                        val userId = signInViewModel.userId.value

                        SessionManager(requireContext()).saveToken(token.toString())
                        SessionManager(requireContext()).saveUserID(userId.toString())
                        findNavController().navigate(R.id.action_signInFragment_to_stayInFragment)
                    }
                    is SignIn.Error -> {
                        binding.signInRootLayout.snackbarError(binding.signIn, result.message, resources.getString(R.string.retry)) {
                            retry(
                                binding.usernameSignInEt,
                                binding.usernameSignInEt,
                                binding.passwordEt
                            )
                        }
                    }
                    is SignIn.Loading -> {}
                }
            }
        }
        if (stayInViewModel.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }
}