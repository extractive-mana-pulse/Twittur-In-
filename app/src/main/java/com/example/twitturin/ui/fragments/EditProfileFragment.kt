package com.example.twitturin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private lateinit var binding : FragmentEditProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.save.setOnClickListener {
            Toast.makeText(requireContext(), "build all logic", Toast.LENGTH_SHORT).show()
        }

        binding.HeaderLayout.setOnClickListener {
//            TODO { open camera or gallery }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }
}