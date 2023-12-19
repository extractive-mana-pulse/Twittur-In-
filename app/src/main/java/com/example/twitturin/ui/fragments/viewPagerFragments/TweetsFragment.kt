package com.example.twitturin.ui.fragments.viewPagerFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding

class TweetsFragment : Fragment() {

    private lateinit var binding: FragmentTweetsBinding

//    private lateinit var viewModel: MainViewModel
//    private val tweetsAdapter by lazy { TweetsAdapter() }
//    private val postAdapter by lazy { PostAdapter(viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTweetsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anView.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            Log.d("Lottie", t.message.toString())
        }
        binding.anView.setAnimation(R.raw.empty_tweets_list)


//        val repository = Repository()
//        val viewModelFactory = ViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
//        updateRecyclerView()


//        if (binding.rcView.isEmpty()){
//            binding.anView.visibility = View.VISIBLE
//
//            binding.anView.setFailureListener { t ->
//                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
//                Log.d("Lottie", t.message.toString())
//            }
//
//            binding.createTweetTv.visibility = View.VISIBLE
//            binding.createTweetTv.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_publicPostFragment)
//            }
//            binding.createTweetTv.paintFlags = binding.createTweetTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
//        }else {
//            updateRecyclerView()
//        }
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun updateRecyclerView() {
//        val sessionManager = SessionManager(requireContext())
//        val userId = sessionManager.getUserId()
//        binding.rcView.adapter = postAdapter
//        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
//        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
//        viewModel.getUserTweet(userId!!)
//        viewModel.responseTweets.observe(requireActivity()) { response ->
//            if (response.isSuccessful) {
//                response.body()?.let { tweets ->
//                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
//                    postAdapter.setData(tweetList)
//                }
//            } else {
//                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
//            }



//        binding.rcView.adapter = postAdapter
//        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
//        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
//
//        viewModel.getUserTweet(userId!!)
//        viewModel.responseTweets.observe(requireActivity()) { response ->
//            if (response.isSuccessful) {
//                Log.d("body",response.body().toString())
//                Log.d("code",response.code().toString())
//                response.body()?.let { tweets ->
//                    Log.d("body",response.body().toString())
//                    Log.d("code",response.code().toString())
//                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
//                    postAdapter.setData(tweetList)
//                }
//            } else {
//                Log.d("body",response.body().toString())
//                Log.d("code",response.code().toString())
//                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
//            }
//        }

//        viewModel.data.observe(viewLifecycleOwner) { result ->
//            when (result) {
//                is UserTweetsResult.Success -> {
//                    val tweets = result.tweets
//                    Log.d("success", result.tweets.toString())
//                    adapter.setData(tweets)
//                }
//
//                is UserTweetsResult.Failure -> {
//                    val exception = result.message
//                    Toast.makeText(requireContext(), exception, Toast.LENGTH_SHORT).show()
//                    Log.d("Failure", exception)
//                }
//            }
//        }
//        viewModel.getPostsFromUser(userId!!)


    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}