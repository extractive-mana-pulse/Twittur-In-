package com.example.twitturin.presentation.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding : FragmentSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // todo { use this build-in method for toolbar }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this

        val pref = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)

        val checkbox = pref.getString("remember","")

        if (checkbox.equals("true")){
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }else if (checkbox.equals("false")){
            Toast.makeText(requireContext(), "checkbox equals to false", Toast.LENGTH_SHORT).show()
        }

        binding.rememberMeCheckBox.setOnCheckedChangeListener { compoundButton, b ->

            if (compoundButton.isChecked){
                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember","true")
                editor.apply()

            }else if(!compoundButton.isChecked){
                val preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember","false")
                editor.apply()

            }
        }
    }

    fun goToSignUp(){
        findNavController().navigate(R.id.signUpFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}