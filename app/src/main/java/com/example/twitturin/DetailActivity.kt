package com.example.twitturin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.twitturin.databinding.ActivityDetailBinding

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
        }
    }
}