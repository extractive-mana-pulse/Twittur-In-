package com.example.twitturin.ui.fragments.loginFragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.FragmentSignInBinding
import com.example.twitturin.ui.sealeds.SignInResult
import com.example.twitturin.viewmodel.SignInViewModel


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        val sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        binding.signIn.setOnClickListener {
            val username = binding.studentIdEt.text.toString()
            val password = binding.passwordEt.text.toString()
            viewModel.signIn(username, password)
        }

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
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val pref = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
        val checkbox = pref.getString("remember", "")

        if (checkbox.equals("true")) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        } else if (checkbox.equals("false")) {
            Log.d("Tag", "hello world")
        }

//                && binding.studentIdEt.text!!.isNotEmpty() && binding.passwordEt.text!!.isNotEmpty()
//        binding.rememberMeCheckBox.setOnCheckedChangeListener { compoundButton, _ ->
//
//            if (compoundButton.isChecked) {
//                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
//                val editor: SharedPreferences.Editor = preferences.edit()
//                editor.putString("remember", "true")
//                editor.apply()
//
//            } else if (!compoundButton.isChecked) {
//                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
//                val editor: SharedPreferences.Editor = preferences.edit()
//                editor.putString("remember", "false")
//                editor.apply()
//            }
//        }

        binding.rememberMeCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            val username = binding.studentIdEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                compoundButton.isChecked = isChecked

                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember", isChecked.toString())
                editor.apply()

                // Additional actions when the checkbox state is updated
            } else {
                compoundButton.isChecked = false

                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember", "false")
                editor.apply()

                // Additional actions when the checkbox is not checked due to empty username or password
            }
        }
    }

    fun chooseKindPage() {
        val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("remember", "false")
        editor.apply()
        findNavController().navigate(R.id.action_signInFragment_to_kindFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}