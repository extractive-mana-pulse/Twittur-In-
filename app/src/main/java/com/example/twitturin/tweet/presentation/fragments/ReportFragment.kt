package com.example.twitturin.tweet.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.databinding.FragmentReportBinding

class ReportFragment : Fragment() {

    private val binding  by lazy { FragmentReportBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backReportBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            radioSpam.setOnCheckedChangeListener { _, _ ->
                testTv.text = scamDescTv.text.toString()
            }

            radioPrivacy.setOnCheckedChangeListener{ _, _ ->
                testTv.text = privacyDescTv.text.toString()
            }

            radioAbuse.setOnCheckedChangeListener{ _, _ ->
                testTv.text = abuseAndHarassmentDescTv.text.toString()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportFragment()
    }
}