package com.example.twitturin.auth.presentation.login.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.twitturin.auth.presentation.login.vm.SignInUiEventViewModel
import com.example.twitturin.auth.presentation.login.vm.SignInViewModel
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

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

            signIn.setOnClickListener { signInViewModel.signIn(usernameSignInEt.text.toString().trim(), passwordEt.text.toString().trim()) }

            signUpTv.setOnClickListener { signInUiEventViewModel.sendKindEvents(SignInUiEvent.OnKindPressed) }
        }

        signInViewModel.signInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignIn.Success -> {

                    val token = signInViewModel.token.value
                    val userId = signInViewModel.userId.value

                    sessionManager.saveToken(token.toString())
                    sessionManager.saveUserID(userId.toString())

                    signInUiEventViewModel.sendKindEvents(SignInUiEvent.OnLoginPressed)

                    lifecycleScope.launchWhenStarted {
                        signInUiEventViewModel.signInEvent.collectLatest {
                            when(it) {
                                SignInUiEvent.StateNoting -> {   }
                                is SignInUiEvent.OnKindPressed -> { findNavController().navigate(R.id.action_signInFragment_to_kindFragment) }
                                is SignInUiEvent.OnLoginPressed -> { findNavController().navigate(R.id.action_signInFragment_to_stayInFragment) }
                            }
                        }
                    }
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

        if (stayInViewModel.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }

        val textWatcher1 = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signIn.isEnabled = !binding.usernameSignInEt.text.isNullOrBlank() && !binding.passwordEt.text.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }

        val textWatcher2 = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signIn.isEnabled = !binding.usernameSignInEt.text.isNullOrBlank() && !binding.passwordEt.text.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }

        binding.usernameSignInEt.addTextChangedListener(textWatcher1)
        binding.passwordEt.addTextChangedListener(textWatcher2)
    }

    private fun retryOperation() {
        binding.usernameSignInEt.text?.clear()
        binding.passwordEt.text?.clear()
    }
}