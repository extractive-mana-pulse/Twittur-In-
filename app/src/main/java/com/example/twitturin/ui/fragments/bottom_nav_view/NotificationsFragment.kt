package com.example.twitturin.ui.fragments.bottom_nav_view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentNotificationsBinding
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var binding : FragmentNotificationsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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