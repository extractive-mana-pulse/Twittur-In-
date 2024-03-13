package com.example.twitturin.auth.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.sealed.SignInResult
import com.example.twitturin.auth.vm.SignInViewModel
import com.example.twitturin.auth.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var stayInViewModel : StayInViewModel
    private val signInViewModel : SignInViewModel by viewModels()
    private val binding by lazy { FragmentSignInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "RestrictedApi", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        checkConnection()
        binding.signInFragment = this

        stayInViewModel = ViewModelProvider(requireActivity())[StayInViewModel::class.java]

        binding.signIn.setOnClickListener {
            val username = binding.usernameSignInEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            signInViewModel.signIn(username, password)
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

        signInViewModel.signInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignInResult.Success -> {
                    val token = signInViewModel.token.value
                    val userId = signInViewModel.userId.value
                    sessionManager.saveToken(token.toString())
                    sessionManager.saveUserID(userId.toString())
                    findNavController().navigate(R.id.action_signInFragment_to_stayInFragment)
                }

                is SignInResult.Error -> {

                    snackbarHelper.snackbarError(
                        view.findViewById(R.id.rootLayout),
                        binding.signIn,
                        result.message,
                        resources.getString(R.string.retry)) {
                        retryOperation()
                    }
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

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.apply {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            val lightMode = AppCompatDelegate.MODE_NIGHT_NO
            statusBarColor = if (currentNightMode == lightMode) {
                ContextCompat.getColor(requireActivity(), R.color.md_theme_light_surface)
            } else {
                ContextCompat.getColor(requireActivity(), R.color.md_theme_dark_surface)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), com.google.android.material.R.color.m3_sys_color_light_surface_container)
    }

//    private fun checkConnection() {
//        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//
//        if (networkInfo != null && networkInfo.isConnected) {
//            findNavController().navigate(R.id.signInFragment)
//        } else {
//            findNavController().navigate(R.id.noInternetFragment)
//        }
//    }
}
