package com.example.twitturin.ui.fragments.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentStudentRegistrationBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.sealeds.SignUpStudentResult
import com.example.twitturin.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class StudentRegistrationFragment : Fragment() {

    private lateinit var viewModel : SignUpViewModel
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val editTextList: MutableList<EditText> = mutableListOf()
    private lateinit var binding : FragmentStudentRegistrationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStudentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.signUp.setOnClickListener {
            val fullname = binding.fullNameEt.text.toString().trim()
            val username = binding.userNameEt.text.toString().trim()
            val studentId = binding.studentIdEt.text.toString().trim()
            val major = binding.planetsSpinner.selectedItem.toString()
            val password = binding.passwordEt.text.toString().trim()
            viewModel.signUp(fullname, username, studentId, major, password, "student")
        }

        editTextList.add(binding.userNameEt)
        editTextList.add(binding.studentIdEt)
        editTextList.add(binding.passwordEt)

        editTextList.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    updateButtonState()
                }
            })
        }

        ArrayAdapter.createFromResource(requireContext(), R.array.major_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.planetsSpinner.adapter = adapter
        }

        binding.planetsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //
            }
        }

        binding.userNameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString()

                if (inputText != null && inputText.contains(" ")) {
                    binding.textInputLayout.error = "No spaces allowed"
                    binding.signUp.isEnabled = false
                } else {
                    binding.textInputLayout.error = null
                    binding.signUp.isEnabled = true
                }
            }
        })

        viewModel.signUpStudentResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignUpStudentResult.Success -> {
                    findNavController().navigate(R.id.action_studentRegistrationFragment_to_signInFragment)
                }

                is SignUpStudentResult.Error -> {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.stud_reg_root_layout),
                        requireActivity().findViewById(R.id.stud_reg_root_layout),
                        error = result.message,
                        ""){ /*better when u write function than just paste it HERE! */ }
                }
            }
        }

        binding.backBtnStudent.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateButtonState() {
        val allFieldsFilled = editTextList.all { editText ->
            editText.text.isNotEmpty()
        }
        binding.signUp.isVisible = allFieldsFilled
    }

    companion object {
        @JvmStatic
        fun newInstance() = StudentRegistrationFragment()
    }
}