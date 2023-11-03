package com.example.twitturin.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding


class PublicPostFragment : Fragment() {

    private lateinit var binding : FragmentPublicPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPublicPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this
    }

    fun cancelBtn(){
        requireActivity().onBackPressed()
    }

    fun tweetBtn(){
        // get logic from api to post a tweet
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicPostFragment()
    }
}