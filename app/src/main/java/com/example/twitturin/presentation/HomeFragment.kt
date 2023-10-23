package com.example.twitturin.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

    }

    fun bottomNavigationViewSetup(){
        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    Toast.makeText(
                        requireContext(),
                        "hello world, this is home page",
                        Toast.LENGTH_SHORT
                    ).show() }
                R.id.search -> {
                    Toast.makeText(
                        requireContext(),
                        "hello world, this is search screen",
                        Toast.LENGTH_SHORT
                    ).show() }
                R.id.messages -> {
                    Toast.makeText(
                        requireContext(),
                        "hello world, this is message section",
                        Toast.LENGTH_SHORT
                    ).show() }
            }
            true
        }
    }

    fun add(){
        findNavController().navigate(R.id.publicPostFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}