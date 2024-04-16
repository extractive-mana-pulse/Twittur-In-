package com.example.twitturin.tweet.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentDetailBinding
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.fragments.FullScreenImageFragment
import com.example.twitturin.profile.presentation.util.snackbar
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.sealed.PostReply
import com.example.twitturin.tweet.presentation.vm.LikeViewModel
import com.example.twitturin.tweet.presentation.vm.TweetViewModel
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject lateinit var sessionManager : SessionManager
    private val likeViewModel : LikeViewModel by viewModels()
    private val tweetViewModel : TweetViewModel by viewModels()
    private val followingViewModel : FollowViewModel by viewModels()
    private val binding  by lazy { FragmentDetailBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(likeViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailFragment = this
        binding.apply {

            val profileImage = arguments?.getString("userAvatar")
            val userFullname = arguments?.getString("fullname")
            val username = arguments?.getString("username")
            val postDescription = arguments?.getString("post_description")
            val createdAt = arguments?.getString("createdAt")
            val updatedAt = arguments?.getString("updatedAt")
            val likes = arguments?.getString("likes")
            val id = arguments?.getString("id")
            val userId = arguments?.getString("userId")

            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("post_description", postDescription).apply()
            sharedPreferences.edit().putString("userImage", profileImage).apply()
            sharedPreferences.edit().putString("username", username).apply()
            sharedPreferences.edit().putString("fullname", userFullname).apply()
            sharedPreferences.edit().putString("userId", userId).apply()
            sharedPreferences.edit().putString("id", id).apply()

            val activateEditText = arguments?.getBoolean("activateEditText", false)

            if (activateEditText!!){
                replyEt.showKeyboard()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

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
                tweetViewModel.postReply(reply!!, id!!, "Bearer $token")
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
                    replyEt.post {
                        val lineCount = replyEt.lineCount
                        val lineHeight = replyEt.lineHeight
                        val desiredHeight = lineCount * lineHeight

                        val layoutParams = replyEt.layoutParams
                        layoutParams.height = desiredHeight
                        replyEt.layoutParams = layoutParams
                    }
                }
            }
            replyEt.addTextChangedListener(textWatcher1)

            tweetViewModel.postReplyResult.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is PostReply.Success -> {
                        replyEt.text?.clear()
                        tweetViewModel.getRepliesOfPost(id!!)
                        postAdapter.notifyDataSetChanged()
                        replyEt.addTextChangedListener(textWatcher1)
                    }

                    is PostReply.Error -> {
                        binding.detailRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.reply_layout),
                            result.message,
                            ""){}
                        replyEt.addTextChangedListener(textWatcher1)
                    }
                }
            }

            try {
                val date = dateFormat.parse(createdAt.toString())
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

            val dateConverter = convertDateFormat(updatedAt.toString())

            whenUpdated.text = dateConverter

            authorAvatar.setOnLongClickListener {

                val fullScreenImageFragment = FullScreenImageFragment()

                binding.authorAvatar.buildDrawingCache()
                val originalBitmap = binding.authorAvatar.drawingCache
                val image = originalBitmap.copy(originalBitmap.config, true)

                val extras = Bundle()
                extras.putParcelable("image", image)
                fullScreenImageFragment.arguments = extras

                fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
                true
            }

            val profileImageUrl = "$profileImage"
            Glide.with(requireContext())
                .load(profileImageUrl)
                .error(R.drawable.not_found)
                .centerCrop()
                .into(authorAvatar)

            authorFullname.text = userFullname ?: "Twittur User"
            authorUsername.text = "@$username"
            detailPostDescription.text = postDescription
            articlePageLikesCounter.text = likes

            followBtn.setOnClickListener {
                followingViewModel.followUsers(userId!!, "Bearer $token")
            }

            followingViewModel.follow.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Follow.Success -> {
                        binding.detailRootLayout.snackbar(
                            requireActivity().findViewById(R.id.reply_layout),
                            message = "now you follow: ${username?.uppercase()}",
                        )
                    }

                    is Follow.Error -> {
                        detailRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.reply_layout),
                            error = result.message,
                            ""
                        ) { /*actionCallBack -> default Unit*/ }
                    }
                }
            }

            detailPageCommentsIcon.setOnClickListener { replyEt.showKeyboard() }

            articlePageHeartIcon.setOnClickListener {
                detailRootLayout.snackbar(
                    requireActivity().findViewById(R.id.reply_layout),
                    resources.getString(R.string.in_progress)
                )
            }

            articlePageShareIcon.setOnClickListener { shareData() }

            moreSettings.setOnClickListener {
                val bottomSheetDialogFragment = MoreSettingsDetailFragment()
                bottomSheetDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "MyBottomSheetDialogFragment"
                )
            }
        }
        updateRecyclerView()
    }

    private fun shareData() {

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", null)

        val intent = Intent(Intent.ACTION_SEND).apply {
            val baseUrl = "https://twitturin.onrender.com/tweets"
            val link = "$baseUrl/$id"

            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(intent, "Choose app:"))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val tweetId = sharedPreferences.getString("id", null)

        binding.articleRcView.adapter = postAdapter
        binding.articleRcView.layoutManager = LinearLayoutManager(requireContext())
        binding.articleRcView.addItemDecoration(DividerItemDecoration(binding.articleRcView.context, DividerItemDecoration.VERTICAL))

        tweetViewModel.getRepliesOfPost(tweetId!!)

        tweetViewModel.repliesOfPosts.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    postAdapter.setData(tweetList)
                    binding.swipeToRefreshArticle.setOnRefreshListener {
                        postAdapter.notifyDataSetChanged()
                        val freshList = tweetList.sortedByDescending { it.createdAt }
                        tweetList.clear()
                        tweetList.addAll(freshList)
                        binding.swipeToRefreshArticle.isRefreshing = false
                    }
                }
            } else {
                binding.detailRootLayout.snackbarError(
                    requireActivity().findViewById(R.id.reply_layout),
                    response.body().toString(),
                    "") {  }
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