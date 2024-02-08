package com.example.twitturin.ui.fragments.bottom_nav_view

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPrivateMessagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.privateMessagesFragment = this
    }

    /** This comment for function back(). This is using data binding and implemented inside xml file. Note: this is my first comment ) */

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