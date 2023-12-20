package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.ui.fragments.bottomsheets.MyBottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n", "PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (isDarkModeActive()){
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_dark_surface_container)
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )

        } else {
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_light_surface_container)
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val fullname = intent.getStringExtra("fullname")
        val username = intent.getStringExtra("username")
        val description = intent.getStringExtra("post_description")
        val createdTime = intent.getStringExtra("createdAt")
        val userImage = intent.getStringExtra("userAvatar")
        val id = intent.getStringExtra("id")
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("id", id).apply()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val date = dateFormat.parse(createdTime.toString())
            val currentTime = System.currentTimeMillis()

            val durationMillis = currentTime - date.time

            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
            val weeks = days / 7

            val durationString = when {
                weeks > 0 -> "$weeks weeks ago"
                days > 0 -> "$days days ago"
                hours > 0 -> "$hours hours ago"
                minutes > 0 -> "$minutes minutes ago"
                else -> "$seconds seconds ago"
            }

            println("Post created $durationString")
            binding.whenCreated.text = durationString
        } catch (e: Exception) {
            println("Invalid date")
            binding.whenCreated.text = "Invalid date"
        }

        binding.apply {

            authorAvatar.setOnLongClickListener {
                authorAvatar.buildDrawingCache()
                val originalBitmap = authorAvatar.drawingCache
                val image = originalBitmap.copy(originalBitmap.config, true)

                val intent = Intent(this@DetailActivity, FullScreenImageActivity::class.java)
                intent.putExtra("image", image)
                startActivity(intent)
                true
            }

            val profileImage = "$userImage"
            Glide.with(this@DetailActivity)
                .load(profileImage)
                .error(R.drawable.not_found)
                .centerCrop()
                .into(authorAvatar)

            authorFullname.text = fullname
            authorUsername.text = "@$username"
            postDescription.text = description

            followBtn.setOnClickListener {
                Toast.makeText(this@DetailActivity, "actualnost birinchi orinda!", Toast.LENGTH_SHORT).show()
            }

            articlePageCommentsIcon.setOnClickListener {
                Toast.makeText(this@DetailActivity, "hello", Toast.LENGTH_SHORT).show()
            }

            articlePageHeartIcon.setOnClickListener {
                Toast.makeText(this@DetailActivity, "what's up?", Toast.LENGTH_SHORT).show()
            }

            articlePageShareIcon.setOnClickListener {
                shareData()
            }

            goBackBtn.setOnClickListener {
                onBackPressed()
            }

            moreSettings.setOnClickListener {
                val bottomSheetDialogFragment = MyBottomSheetDialogFragment()
                bottomSheetDialogFragment.show(
                    supportFragmentManager,
                    "MyBottomSheetDialogFragment"
                )
            }
        }
    }

    private fun isDarkModeActive(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun shareData() {

        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", null)

        val intent = Intent(Intent.ACTION_SEND)
        val baseUrl = "https://twitturin.onrender.com/tweets"
        val link = "$baseUrl/$id"

        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent,"Choose app:"))
    }
}