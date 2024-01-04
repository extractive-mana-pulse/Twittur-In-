package com.example.twitturin.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.twitturin.R
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
                Toast.makeText(this@ReportActivity, "spam $isChecked", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@ReportActivity, "spam ${buttonView.text}", Toast.LENGTH_SHORT).show()
            }
            radioPrivacy.setOnCheckedChangeListener{ buttonView, isChecked ->
                Toast.makeText(this@ReportActivity, "privacy $isChecked", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@ReportActivity, "privacy ${buttonView.text}", Toast.LENGTH_SHORT).show()
            }
            radioAbuse.setOnCheckedChangeListener{ buttonView, isChecked ->
                Toast.makeText(this@ReportActivity, "abuse $isChecked", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@ReportActivity, "abuse ${buttonView.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}