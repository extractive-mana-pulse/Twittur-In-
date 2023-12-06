package com.example.twitturin.ui.fragments.viewPagerFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentLikesBinding

class LikesFragment : Fragment() {

    private lateinit var binding : FragmentLikesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLikesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anView.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            Log.d("Lottie", t.message.toString())
        }
        binding.anView.setAnimation(R.raw.empty_likes_list)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LikesFragment()
    }
}