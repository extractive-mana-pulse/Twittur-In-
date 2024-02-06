package com.example.twitturin.ui.fragments.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.sealeds.SignUpProfResult
import com.example.twitturin.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfessorRegistrationFragment : Fragment() {

    private lateinit var viewModel : SignUpViewModel
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var binding : FragmentProfessorRegistrationBinding
    private val profEditTextList: MutableList<EditText> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfessorRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]
        binding.signUpProf.setOnClickListener {
            val fullName = binding.profFullnameEt.text.toString().trim()
            val username = binding.profUsernameEt.text.toString().trim()
            val subject = binding.profSubjectEt.text.toString().trim()
            val password = binding.profPasswordEt.text.toString().trim()
            viewModel.signUpProf(fullName, username, subject, password, "teacher")
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
                    updateButtonState()
                }
            })
        }

        binding.profUsernameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString()

                if (inputText != null && inputText.contains(" ")) {
                    binding.profUsernameInputLayout.error = "No spaces allowed"
                    binding.signUpProf.isEnabled = false
                } else {
                    binding.profUsernameInputLayout.error = null
                    binding.signUpProf.isEnabled = true
                }
            }
        })

        viewModel.profRegResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignUpProfResult.Success -> {
                    findNavController().navigate(R.id.action_professorRegistrationFragment_to_signInFragment)
                }

                is SignUpProfResult.Error -> {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.prof_reg_root_layout),
                        requireActivity().findViewById(R.id.prof_reg_root_layout),
                        error = result.message,
                        ""){}
                }
            }
        }

        binding.backBtnProf.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = profEditTextList.all { editText ->
            editText.text.isNotBlank()
        }
        binding.signUpProf.isVisible = allFieldsFilled
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfessorRegistrationFragment()
    }
}