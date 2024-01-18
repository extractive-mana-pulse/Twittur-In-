package com.example.twitturin.ui.fragments.loginFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.sealeds.SignInResult
import com.example.twitturin.viewmodel.SignInViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var viewModel: SignInViewModel
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "RestrictedApi", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        binding.signIn.setOnClickListener {
            val username = binding.usernameSignInEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            viewModel.signIn(username, password)
        }

        val textWatcher1 = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Enable or disable the button based on the EditText fields' contents
                binding.signIn.isEnabled = !binding.usernameSignInEt.text.isNullOrBlank() && !binding.passwordEt.text.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        }

        val textWatcher2 = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Enable or disable the button based on the EditText fields' contents
                binding.signIn.isEnabled = !binding.usernameSignInEt.text.isNullOrBlank() && !binding.passwordEt.text.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        }

        binding.usernameSignInEt.addTextChangedListener(textWatcher1)
        binding.passwordEt.addTextChangedListener(textWatcher2)

        viewModel.signInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignInResult.Success -> {
                    val token = viewModel.token.value
                    val userId = viewModel.userId.value
                    sessionManager.saveToken(token.toString())
                    sessionManager.saveUserID(userId.toString())
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                }

                is SignInResult.Error -> {

                    snackbarHelper.snackbarError(
                        view.findViewById(R.id.rootLayout),
                        binding.signIn,
                        result.message,
                        "Retry") { retryOperation() }

                }
            }
        }
    }

    private fun retryOperation() {
        binding.usernameSignInEt.text?.clear()
        binding.passwordEt.text?.clear()
    }

    fun chooseKindPage() {
        findNavController().navigate(R.id.action_signInFragment_to_kindFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}
