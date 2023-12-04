package com.example.twitturin.ui.fragments.viewPagerFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.ui.adapters.TweetsAdapter
import com.example.twitturin.ui.viewModels.ProfileViewModel

class TweetsFragment : Fragment() {

    private lateinit var binding: FragmentTweetsBinding

    private lateinit var viewModel : ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTweetsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcView.layoutManager = LinearLayoutManager(context)
        val adapter = TweetsAdapter(emptyList())
        binding.rcView.adapter = adapter

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

    }


    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}