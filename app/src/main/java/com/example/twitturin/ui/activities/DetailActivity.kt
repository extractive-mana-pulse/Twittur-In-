package com.example.twitturin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.ui.fragments.bottomsheets.MyBottomSheetDialogFragment

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fullname = intent.getStringExtra("fullname")
        val username = intent.getStringExtra("username")
        val description = intent.getStringExtra("post_description")
        val link = intent.getStringExtra("link")
        val createdTime = intent.getStringExtra("createdAt")

        binding.apply {
            authorFullname.text = fullname
            authorUsername.text = username
            postDescription.text = description
            whenCreated.text = createdTime

            followBtn.setOnClickListener {
                Toast.makeText(this@DetailActivity, "actualnost birinchi orinda!", Toast.LENGTH_SHORT).show()
            }

            goBackBtn.setOnClickListener {
                onBackPressed()
            }

            moreSettings.setOnClickListener {
                val bottomSheetDialogFragment = MyBottomSheetDialogFragment()
                bottomSheetDialogFragment.show(supportFragmentManager, "MyBottomSheetDialogFragment")
            }
        }
    }
}