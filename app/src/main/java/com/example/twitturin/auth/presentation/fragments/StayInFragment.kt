package com.example.twitturin.auth.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.vm.StayInViewModel
import com.example.twitturin.databinding.FragmentStayInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StayInFragment : Fragment() {

    private lateinit var stayInViewModel: StayInViewModel
    private val binding by lazy { FragmentStayInBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stayInViewModel = ViewModelProvider(requireActivity())[StayInViewModel::class.java]

        binding.apply {

            approveBtn.setOnClickListener {
                stayInViewModel.setUserLoggedIn(true)
                findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
            }

            laterBtn.setOnClickListener {
                stayInViewModel.setUserLoggedIn(false)
                findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
            }
        }
    }
}