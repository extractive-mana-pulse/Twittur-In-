package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.fragments.bottomsheets.MyBottomSheetDialogFragment
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.ui.sealeds.PostReply
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(this@DetailActivity) }
    private lateinit var followViewModel: FollowUserViewModel
    private lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n", "PrivateResource", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userImage = intent.getStringExtra("userAvatar")
        val fullname = intent.getStringExtra("fullname")
        val username = intent.getStringExtra("username")
        val description = intent.getStringExtra("post_description")
        val createdTime = intent.getStringExtra("createdAt")
        val likes = intent.getStringExtra("likes")
        val id = intent.getStringExtra("id")
        val userId = intent.getStringExtra("userId")

        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("username", username).apply()
        sharedPreferences.edit().putString("id", id).apply()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this@DetailActivity, viewModelFactory)[MainViewModel::class.java]

        val sessionManager = SessionManager(this@DetailActivity)
        val token = sessionManager.getToken()

        binding.sentReply.setOnClickListener {
            val handler = Handler()
            val reply = binding.replyEt.text.toString()

            if (reply.isEmpty()){
                Toast.makeText(this@DetailActivity, "reply field should not be empty", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.postReply(reply, id!!, "Bearer $token")
            }

            binding.sentReply.isEnabled = false
            handler.postDelayed({ binding.sentReply.isEnabled = true }, 3000)
        }

        viewModel.postReplyResult.observe(this@DetailActivity) { result ->

            when (result) {
                is PostReply.Success -> {
                    binding.replyEt.text.clear()
                    viewModel.getRepliesOfPost(id!!)
                    postAdapter.notifyDataSetChanged()
                }

                is PostReply.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    binding.sentReply.isEnabled = true
                }
            }
        }


        followViewModel = ViewModelProvider(this@DetailActivity)[FollowUserViewModel::class.java]
        updateRecyclerView()

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

            authorFullname.text = fullname ?: "Twittur User"
            authorUsername.text = "@$username"
            postDescription.text = description
            articlePageLikesCounter.text = likes

            followBtn.setOnClickListener {
                followViewModel.followUsers(userId!!, "Bearer $token")
            }

            followViewModel.followResult.observe(this@DetailActivity) { result ->
                when (result) {
                    is FollowResult.Success -> {
                        Toast.makeText(this@DetailActivity, "now you follow: $username", Toast.LENGTH_SHORT).show()
                    }
                    is FollowResult.Error -> {
                        val errorMessage = result.message
                        Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            articlePageCommentsIcon.setOnClickListener {
                Toast.makeText(this@DetailActivity, "in progress", Toast.LENGTH_SHORT).show()
            }

            articlePageHeartIcon.setOnClickListener {
                Toast.makeText(this@DetailActivity, "in progress", Toast.LENGTH_SHORT).show()
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


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val tweetId = sharedPreferences.getString("id", null)

        binding.articleRcView.adapter = postAdapter
        binding.articleRcView.layoutManager = LinearLayoutManager(this)
        binding.articleRcView.addItemDecoration(DividerItemDecoration(binding.articleRcView.context, DividerItemDecoration.VERTICAL))

        viewModel.getRepliesOfPost(tweetId!!)

        viewModel.repliesOfPosts.observe(this) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    postAdapter.setData(tweetList)
                    binding.swipeToRefreshArticle.setOnRefreshListener {
                        postAdapter.notifyDataSetChanged()
                        viewModel.getRepliesOfPost(tweetId)
                        binding.swipeToRefreshArticle.isRefreshing = false
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                    }
                }
            } else {
                Toast.makeText(this, response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}