package com.example.twitturin.ui.fragments.viewPagerFragments

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding
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

//        binding.rcView.layoutManager = LinearLayoutManager(context)
//        val adapter = TweetsAdapter(emptyList())
//        binding.rcView.adapter = adapter

//        binding.rcView.visibility = View.GONE

        binding.anView.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            Log.d("Lottie", t.message.toString())
        }
        binding.anView.setAnimation(R.raw.empty_tweets_list)

        binding.createTweetTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_publicPostFragment)
        }
        binding.createTweetTv.paintFlags = binding.createTweetTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG

    }

//    private fun setupRecyclerView() {
//        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
//        binding.rcView.layoutManager = LinearLayoutManager(activity)
//        binding.rcView.adapter = TweetsAdapter(emptyList())
//        viewModel.readAllData.observe(requireActivity()) { data ->
//            adapter.setData(data)
//        }
//    }


    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}