package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.fragments.bottom_sheets.MoreSettingsDetailFragment
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.ui.sealeds.PostReply
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.UsersResult
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val lViewModel: LikeViewModel by viewModels()
    private val allUsers = mutableListOf<String>()
    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager: SessionManager
    private val followedUsersList = mutableListOf<String>()
    private lateinit var followViewModel: FollowUserViewModel
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(lViewModel,this@DetailActivity) }

    @SuppressLint("SetTextI18n", "PrivateResource", "NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val userImage = intent.getStringExtra("userAvatar")
            val fullname = intent.getStringExtra("fullname")
            val username = intent.getStringExtra("username")
            val description = intent.getStringExtra("post_description")
            val createdTime = intent.getStringExtra("createdAt")
            val updatedTime = intent.getStringExtra("updatedAt")
            val likes = intent.getStringExtra("likes")
            val id = intent.getStringExtra("id")
            val userId = intent.getStringExtra("userId")

            val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("post_description", description).apply()
            sharedPreferences.edit().putString("userImage", userImage).apply()
            sharedPreferences.edit().putString("username", username).apply()
            sharedPreferences.edit().putString("fullname", fullname).apply()
            sharedPreferences.edit().putString("userId", userId).apply()
            sharedPreferences.edit().putString("id", id).apply()

            val activateEditText = intent.getBooleanExtra("activateEditText", false)
            if (activateEditText) {
                replyEt.post {
                    replyEt.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(replyEt, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val repository = Repository()
            val viewModelFactory = ViewModelFactory(repository)
            followViewModel = ViewModelProvider(this@DetailActivity)[FollowUserViewModel::class.java]
            viewModel = ViewModelProvider(this@DetailActivity, viewModelFactory)[MainViewModel::class.java]

            val token = sessionManager.getToken()
            val userId2 = sessionManager.getUserId()

            if (userId == userId2) {
                followBtn.visibility = View.GONE
            } else {
                followBtn.visibility = View.VISIBLE
            }

            sentReply.isEnabled = false

            sentReply.setOnClickListener {
                val reply = replyEt.text?.toString()?.trim()
                viewModel.postReply(reply!!, id!!, "Bearer $token")
                sentReply.isEnabled = false
            }

            val textWatcher1 = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not used
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    sentReply.isEnabled = !replyEt.text.isNullOrBlank()
                }

                override fun afterTextChanged(s: Editable?) {
                    // Not used
                }
            }
            replyEt.addTextChangedListener(textWatcher1)

            viewModel.postReplyResult.observe(this@DetailActivity) { result ->

                when (result) {
                    is PostReply.Success -> {
                        replyEt.text?.clear()
                        viewModel.getRepliesOfPost(id!!)
                        postAdapter.notifyDataSetChanged()
                        replyEt.addTextChangedListener(textWatcher1)
                    }

                    is PostReply.Error -> {
                        snackbarHelper.snackbarError(
                            findViewById(R.id.detail_root_layout),
                            findViewById(R.id.reply_layout),
                            result.message,
                            ""
                        ) {}
                        replyEt.addTextChangedListener(textWatcher1)
                    }
                }
            }

            try {
                val date = dateFormat.parse(createdTime.toString())
                val currentTime = System.currentTimeMillis()

                val durationMillis = currentTime - date!!.time

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

            val dateConverter = convertDateFormat(updatedTime.toString())
            whenUpdated.text = dateConverter

            authorAvatar.setOnLongClickListener {
                authorAvatar.buildDrawingCache()
                val originalBitmap: Bitmap = authorAvatar.drawingCache
                val image = originalBitmap.copy(originalBitmap.config, true)

                val intent = Intent(this@DetailActivity, FullScreenImageActivity::class.java)
                intent.putExtra("image", image)
                startActivity(intent)
                true
            }

            val profileImageUrl = "$userImage"
            Glide.with(this@DetailActivity)
                .load(profileImageUrl)
                .error(R.drawable.not_found)
                .centerCrop()
                .into(authorAvatar)

            authorFullname.text = fullname ?: "Twittur User"
            authorUsername.text = "@$username"
            postDescription.text = description
            articlePageLikesCounter.text = likes

            // TODO so when follow button pressed. add user's username to "followedList" after check list like below that do logic
            // TODO also use getAll Users endpoint to do that !

            viewModel.getAllUsers()

            viewModel.usersResult.observe(this@DetailActivity) { result ->
                when (result) {
                    is UsersResult.Success -> {
                        val users = result.users
                        allUsers.add(users.toString())
                        Log.d("test", allUsers.toString())
                    }
                    is UsersResult.Error -> {
                        val errorMessage = result.errorMessage
                        Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            followBtn.setOnClickListener {
                followViewModel.followUsers(userId!!, "Bearer $token")
            }

            followViewModel.followResult.observe(this@DetailActivity) { result ->
                when (result) {
                    is FollowResult.Success -> {
                        snackbarHelper.snackbar(
                            findViewById(R.id.detail_root_layout),
                            findViewById(R.id.reply_layout),
                            message = "now you follow: ${username?.uppercase()}"
                        )
                        followedUsersList.add(username.toString())
                        Log.d("followed users list", followedUsersList.toString())
                    }

                    is FollowResult.Error -> {
                        snackbarHelper.snackbarError(
                            findViewById(R.id.detail_root_layout),
                            findViewById(R.id.reply_layout),
                            result.message,
                            ""
                        ) {}
                    }
                }
            }

            articlePageCommentsIcon.setOnClickListener {
                snackbarHelper.snackbar(
                    findViewById(R.id.detail_root_layout),
                    findViewById(R.id.reply_layout),
                    message = resources.getString(R.string.in_progress)
                )
            }

            articlePageHeartIcon.setOnClickListener {
                snackbarHelper.snackbar(
                    findViewById(R.id.detail_root_layout),
                    findViewById(R.id.reply_layout),
                    message = resources.getString(R.string.in_progress)
                )
            }

            articlePageShareIcon.setOnClickListener {
                shareData()
            }

            goBackBtn.setOnClickListener {
                onBackPressed()
            }

            moreSettings.setOnClickListener {
                val bottomSheetDialogFragment = MoreSettingsDetailFragment()
                bottomSheetDialogFragment.show(
                    supportFragmentManager,
                    "MyBottomSheetDialogFragment"
                )
            }
        }
        updateRecyclerView()
    }

    private fun shareData() {

        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", null)

        val intent = Intent(Intent.ACTION_SEND)
        val baseUrl = "https://twitturin.onrender.com/tweets"
        val link = "$baseUrl/$id"

        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent, "Choose app:"))
    }

    private fun checkFollowedUsersStatus() {
        val username = intent.getStringExtra("username")
        if (followedUsersList.contains(username)) {
            binding.unfollowBtn.visibility = View.VISIBLE
            binding.followBtn.visibility = View.INVISIBLE
        } else {
            binding.unfollowBtn.visibility = View.INVISIBLE
            binding.followBtn.visibility = View.VISIBLE
        }
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
                snackbarHelper.snackbarError(
                    findViewById(R.id.detail_root_layout),
                    findViewById(R.id.reply_layout),
                    response.message().toString(),
                    ""){}
            }
        }
    }

    fun convertDateFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}