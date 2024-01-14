package com.example.twitturin.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityReportBinding

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

            radioSpam.setOnCheckedChangeListener { buttonView, isChecked ->
                testTv.text = scamDescTv.text.toString()
            }

            radioPrivacy.setOnCheckedChangeListener{ buttonView, isChecked ->
                testTv.text = privacyDescTv.text.toString()
            }

            radioAbuse.setOnCheckedChangeListener{ buttonView, isChecked ->
                testTv.text = abuseAndHarassmentDescTv.text.toString()
            }
        }
    }
}