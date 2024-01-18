package com.example.twitturin.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityFullScreenImageBinding

@Suppress("DEPRECATION")
class FullScreenImageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFullScreenImageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageBitmap = intent.getParcelableExtra<Bitmap>("image")
        binding.apply {

            imageFullScreenArticle.setImageBitmap(imageBitmap)

            fullScreenArticlePageBackBtn.setOnClickListener {
                onBackPressed()
            }
        }
    }
}