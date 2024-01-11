package com.example.twitturin.ui.fragments.loginFragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.ui.sealeds.SignInResult
import com.example.twitturin.viewmodel.SignInViewModel
import com.google.android.material.snackbar.Snackbar


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "RestrictedApi", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        val sessionManager = SessionManager(requireContext())
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

                    val error = result.message
                    val rootView: View = requireActivity().findViewById(R.id.rootLayout)
                    val duration = Snackbar.LENGTH_SHORT
                    val actionText = "Retry"

                    val snackbar = Snackbar
                        .make(rootView, error, duration)
                        .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
                        .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
                        .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
                        .setAnchorView(binding.signIn)
                        .setAction(actionText) {
                        binding.usernameSignInEt.text?.clear()
                        binding.passwordEt.text?.clear()
                    }
                    snackbar.show()
                }
            }
        }
    }

    fun chooseKindPage() {
        findNavController().navigate(R.id.action_signInFragment_to_kindFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}