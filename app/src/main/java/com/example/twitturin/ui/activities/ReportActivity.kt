package com.example.twitturin.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityReportBinding

@Suppress("DEPRECATION")
class ReportActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backReportBtn.setOnClickListener {
                onBackPressed()
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
}