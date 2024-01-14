package com.example.twitturin.ui.fragments.loginFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfessorRegistrationBinding
import com.example.twitturin.ui.sealeds.SignUpProfResult
import com.example.twitturin.viewmodel.SignUpViewModel
import com.google.android.material.snackbar.Snackbar

class ProfessorRegistrationFragment : Fragment() {

    private lateinit var binding : FragmentProfessorRegistrationBinding
    private val profEditTextList: MutableList<EditText> = mutableListOf()
    private lateinit var viewModel : SignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                    binding.textInputLayout1.error = "No spaces allowed"
                    binding.signUpProf.isEnabled = false
                } else {
                    binding.textInputLayout1.error = null
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
                    snackbarError(result.message)
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

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.prof_reg_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfessorRegistrationFragment()
    }
}