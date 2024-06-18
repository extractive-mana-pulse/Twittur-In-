package com.example.twitturin.auth.presentation.login.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.login.sealed.SignIn
import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent
import com.example.twitturin.auth.presentation.login.util.login
import com.example.twitturin.auth.presentation.login.vm.SignInUiEventViewModel
import com.example.twitturin.auth.presentation.login.vm.SignInViewModel
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    private val signInViewModel : SignInViewModel by viewModels()
    private val stayInViewModel : StayInViewModel by viewModels()
    private val signInUiEventViewModel : SignInUiEventViewModel by viewModels()
    private val binding by lazy { FragmentSignInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "RestrictedApi", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        binding.apply {

            signIn.setOnClickListener { signInUiEventViewModel.onLoginPressed()/*signInUiEventViewModel.sendKindEvents(SignInUiEvent.OnLoginPressed)*/ }

            signUpTv.setOnClickListener { signInUiEventViewModel.onKindPressed()/*signInUiEventViewModel.sendKindEvents(SignInUiEvent.OnKindPressed)*/ }

            usernameSignInEt.login(usernameSignInEt, passwordEt, signIn)

            passwordEt.login(usernameSignInEt, passwordEt, signIn)
        }

        // Observe the signInEvent flow and handle the events
        viewLifecycleOwner.lifecycleScope.launch {
            signInUiEventViewModel.signInEvent.collect { event ->
                when (event) {
                    is SignInUiEvent.OnLoginPressed -> {
                        val username = binding.usernameSignInEt.text.toString().trim()
                        val password = binding.passwordEt.text.toString().trim()
                        signInViewModel.signIn(username, password)

                        signInViewModel.signInResult.observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is SignIn.Success -> {

                                    val token = signInViewModel.token.value
                                    val userId = signInViewModel.userId.value

                                    sessionManager.saveToken(token.toString())
                                    sessionManager.saveUserID(userId.toString())
                                    findNavController().navigate(R.id.action_signInFragment_to_stayInFragment)
                                }

                                is SignIn.Error -> {

                                    binding.signInRootLayout.snackbarError(
                                        view.findViewById(R.id.sign_in),
                                        error = result.message,
                                        resources.getString(R.string.retry)
                                    ) { retryOperation() }
                                }
                            }
                        }
                    }
                    is SignInUiEvent.OnKindPressed -> { findNavController().navigate(R.id.action_signInFragment_to_kindFragment) }
                    is SignInUiEvent.StateNoting -> {  }
                }
            }
        }

        // Set up click listeners for the buttons
//        binding.signIn.setOnClickListener { signInUiEventViewModel.onLoginPressed() }
//        binding.signUpTv.setOnClickListener { signInUiEventViewModel.onKindPressed() }

//        lifecycleScope.launchWhenStarted {
//            signInUiEventViewModel.signInEvent.collect {
//                when(it) {
//                    is SignInUiEvent.StateNoting -> {   }
//
//                    is SignInUiEvent.OnKindPressed -> { findNavController().navigate(R.id.action_signInFragment_to_kindFragment) }
//
//                    is SignInUiEvent.OnLoginPressed -> {
//                        val username = binding.usernameSignInEt.text.toString().trim()
//                        val password = binding.passwordEt.text.toString().trim()
//                        signInViewModel.signIn(username, password)
//
//                        signInViewModel.signInResult.observe(viewLifecycleOwner) { result ->
//                            when (result) {
//                                is SignIn.Success -> {
//
//                                    val token = signInViewModel.token.value
//                                    val userId = signInViewModel.userId.value
//
//                                    sessionManager.saveToken(token.toString())
//                                    sessionManager.saveUserID(userId.toString())
//                                    findNavController().navigate(R.id.action_signInFragment_to_stayInFragment)
//                                }
//
//                                is SignIn.Error -> {
//
//                                    binding.signInRootLayout.snackbarError(
//                                        view.findViewById(R.id.sign_in),
//                                        error = result.message,
//                                        resources.getString(R.string.retry)
//                                    ) { retryOperation() }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        // fix bug with navigation in sign in page.
        if (stayInViewModel.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }

    private fun retryOperation() {
        binding.usernameSignInEt.text?.clear()
        binding.passwordEt.text?.clear()
    }
}