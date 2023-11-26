package com.example.twitturin.presentation.fragments.bottomNavigationView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentNotificationsBinding
import com.example.twitturin.presentation.data.users.UsersItem

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

    fun goToProfile(){
        findNavController().navigate(R.id.profileFragment)
    }
    companion object {
        @JvmStatic
        fun newInstance() = NotificationsFragment()
    }
}