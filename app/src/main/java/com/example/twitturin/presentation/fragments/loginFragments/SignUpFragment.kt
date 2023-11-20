package com.example.twitturin.presentation.fragments.loginFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSignUpBinding
import com.example.twitturin.presentation.mvvm.MainViewModel
import com.example.twitturin.presentation.mvvm.Repository
import com.example.twitturin.presentation.mvvm.ViewModelFactory
import com.example.twitturin.presentation.sealeds.SignUpResult

class SignUpFragment : Fragment() {

    private lateinit var binding : FragmentSignUpBinding

    private lateinit var viewModel : MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        binding.signUp.setOnClickListener {
            val username = binding.userNameEt.text.toString()
            val studentId = binding.studentIdEt.text.toString()
            val major = binding.majorEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            val kind = binding.kind.text.toString()
            viewModel.signUp(username, studentId, major, email, password, kind)
        }

        viewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignUpResult.Success -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                }

                is SignUpResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}