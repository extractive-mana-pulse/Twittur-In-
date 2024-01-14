package com.example.twitturin.ui.fragments.loginFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentKindBinding

class KindFragment : Fragment() {

    private lateinit var binding : FragmentKindBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentKindBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            professorKindBtn.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_professorRegistrationFragment)
            }

            studentKindBtn.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_studentRegistrationFragment)
            }

            backBtnKind.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_signInFragment)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = KindFragment()
    }
}