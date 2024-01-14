package com.example.twitturin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar


class PublicPostFragment : Fragment() {

    private lateinit var binding : FragmentPublicPostBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPublicPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()
        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        binding.btnTweet.setOnClickListener {
                binding.btnTweet.isEnabled = false
                val tweetContent = binding.contentEt.text.toString()
                viewModel.postTheTweet(tweetContent, "Bearer $token")
        }

        viewModel.postTweetResult.observe(viewLifecycleOwner) { result ->

            when (result) {
                is PostTweet.Success -> {
                    findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                }

                is PostTweet.Error -> {
                    snackbarError(result.message)
                    binding.btnTweet.isEnabled = true
                }
            }
        }
    }

    fun cancelBtn(){
        requireActivity().onBackPressed()
    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.public_post_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicPostFragment()
    }
}