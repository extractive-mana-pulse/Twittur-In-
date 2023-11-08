package com.example.twitturin.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPrivateMessagesBinding


class PrivateMessagesFragment : Fragment() {

    private lateinit var binding : FragmentPrivateMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPrivateMessagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.privateMessagesFragment = this
    }

    fun back(){
        requireActivity().onBackPressed()
    }

    fun goToProfile(){
        findNavController().navigate(R.id.profileFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PrivateMessagesFragment()
    }
}