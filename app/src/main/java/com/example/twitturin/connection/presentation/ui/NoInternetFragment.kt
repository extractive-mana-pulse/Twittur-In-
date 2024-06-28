package com.example.twitturin.connection.presentation.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentNoInternetBinding

class NoInternetFragment : Fragment() {

    private val binding by lazy { FragmentNoInternetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tryAgainBtn.setOnClickListener {
                checkConnection()
            }
        }
    }

    private fun checkConnection() {

        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            findNavController().navigate(R.id.signInFragment)
        } else {
            findNavController().navigate(R.id.noInternetFragment)
        }
    }
}