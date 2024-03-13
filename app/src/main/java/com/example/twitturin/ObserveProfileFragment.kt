package com.example.twitturin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.databinding.FragmentObserveProfileBinding

class ObserveProfileFragment : Fragment() {

    private val binding by lazy { FragmentObserveProfileBinding.inflate(layoutInflater) }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val userFullname = arguments?.getString("fullname")
            val username = arguments?.getString("username")
            val id = arguments?.getString("id")

            test1.text = userFullname.toString()
            test2.text = username
            test3.text = id

        }
    }
}