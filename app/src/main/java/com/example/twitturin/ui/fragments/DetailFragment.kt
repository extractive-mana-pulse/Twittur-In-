package com.example.twitturin.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentDetailBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.fragments.bottom_sheets.MoreSettingsDetailFragment
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.ui.sealeds.PostReply
import com.example.twitturin.ui.sealeds.UsersResult
import com.example.twitturin.viewmodel.FollowingViewModel
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private val allUsers = mutableListOf<String>()
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val lViewModel: LikeViewModel by viewModels()
    private val followedUsersList = mutableListOf<String>()
    private lateinit var followViewModel: FollowingViewModel
    private val binding  by lazy { FragmentDetailBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(lViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            if (activateEditText!!) {
                replyEt.post {
                    replyEt.requestFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(replyEt, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val repository = Repository()
            val viewModelFactory = ViewModelFactory(repository)
            followViewModel = ViewModelProvider(requireActivity())[FollowingViewModel::class.java]
            viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

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

            viewModel.postReplyResult.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is PostReply.Success -> {
                        replyEt.text?.clear()
                        viewModel.getRepliesOfPost(id!!)
                        postAdapter.notifyDataSetChanged()
                        replyEt.addTextChangedListener(textWatcher1)
                    }

                    is PostReply.Error -> {
                        snackbarHelper.snackbarError(
                            requireActivity().findViewById(R.id.detail_root_layout),
                            requireActivity().findViewById(R.id.reply_layout),
                            result.message,
                            ""
                        ) {}
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

            // TODO so when follow button pressed. add user's username to "followedList" after check list like below that do logic
            // TODO also use getAll Users endpoint to do that !

            viewModel.getAllUsers()

            viewModel.usersResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UsersResult.Success -> {
                        val users = result.users
                        allUsers.add(users.toString())
                        Log.d("test", allUsers.toString())
                    }
                    is UsersResult.Error -> {
                        val errorMessage = result.errorMessage
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            followBtn.setOnClickListener {
                followViewModel.followUsers(userId!!, "Bearer $token")
            }

            followViewModel.followResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is FollowResult.Success -> {
                        snackbarHelper.snackbar(
                            requireActivity().findViewById(R.id.detail_root_layout),
                            requireActivity().findViewById(R.id.reply_layout),
                            message = "now you follow: ${username?.uppercase()}"
                        )
                        followedUsersList.add(username.toString())
                        Log.d("followed users list", followedUsersList.toString())
                    }

                    is FollowResult.Error -> {
                        snackbarHelper.snackbarError(
                            requireActivity().findViewById(R.id.detail_root_layout),
                            requireActivity().findViewById(R.id.reply_layout),
                            result.message,
                            ""
                        ) {}
                    }
                }
            }

            articlePageCommentsIcon.setOnClickListener {
                snackbarHelper.snackbar(
                    requireActivity().findViewById(R.id.detail_root_layout),
                    requireActivity().findViewById(R.id.reply_layout),
                    message = resources.getString(R.string.in_progress)
                )
            }

            articlePageHeartIcon.setOnClickListener {
                snackbarHelper.snackbar(
                    requireActivity().findViewById(R.id.detail_root_layout),
                    requireActivity().findViewById(R.id.reply_layout),
                    message = resources.getString(R.string.in_progress)
                )
            }

            articlePageShareIcon.setOnClickListener {
                shareData()
            }
            goBackBtn.setOnClickListener {
                findNavController().popBackStack()
            }

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

        val intent = Intent(Intent.ACTION_SEND)
        val baseUrl = "https://twitturin.onrender.com/tweets"
        val link = "$baseUrl/$id"

        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent, "Choose app:"))
    }

    private fun checkFollowedUsersStatus() {
        val username = arguments?.getString("username")
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
        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val tweetId = sharedPreferences.getString("id", null)

        binding.articleRcView.adapter = postAdapter
        binding.articleRcView.layoutManager = LinearLayoutManager(requireContext())
        binding.articleRcView.addItemDecoration(DividerItemDecoration(binding.articleRcView.context, DividerItemDecoration.VERTICAL))

        viewModel.getRepliesOfPost(tweetId!!)

        viewModel.repliesOfPosts.observe(viewLifecycleOwner) { response ->
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
                snackbarHelper.snackbarError(
                    requireActivity().findViewById(R.id.detail_root_layout),
                    requireActivity().findViewById(R.id.reply_layout),
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

    companion object {
        @JvmStatic
        fun newInstance() = DetailFragment()
    }
}