package com.example.twitturin.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private lateinit var binding : FragmentNotificationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationsFragment = this


    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationsFragment()
    }
}