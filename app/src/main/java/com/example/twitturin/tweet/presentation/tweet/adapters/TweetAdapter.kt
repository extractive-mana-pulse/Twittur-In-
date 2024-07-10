package com.example.twitturin.tweet.presentation.tweet.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewUserTweetsBinding
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.converter
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.like.vm.LikeViewModel
import com.example.twitturin.tweet.presentation.tweet.util.formatCreatedAtShortened
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class TweetAdapter @Inject constructor(
    private val tweetViewModel : TweetViewModel,
    private val parentLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    private lateinit var likeViewModel: LikeViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewUserTweetsBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Tweet>(){

        override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_user_tweets, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DiscouragedPrivateApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = differ.currentList[position]
        val baseUrl = "https://twitturin.onrender.com/tweets"

//        var likeCount: Int? = item.likes
//        var isLiked: Boolean = false

        holder.binding.apply {
            item.apply {

                Glide.with(context)
                    .load("${author?.profilePicture}")
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(userAvatarOwnTweet)

                postHeartCounter.text = likes.toString()
                postDescriptionUserOwnTweet.text = content
                postCommentsCounter.text = replyCount.toString()
                usernameTvUserOwnTweet.text = "@" + author?.username
                fullNameTvUserOwnTweet.text = author?.fullName ?: R.string.default_user_fullname.toString()

                createdAtTv.text = createdAt.formatCreatedAtShortened()

                holder.itemView.setOnClickListener {

                    val bundle = Bundle().apply {
                        putString("id", id)
                        putString("userId", author?.id)
                        putString("createdAt", createdAt)
                        putString("updatedAt", updatedAt)
                        putString("likes", likes.toString())
                        putString("post_description", content)
                        putString("username", author?.username)
                        putString("fullname", author?.fullName)
                        putString("userAvatar", author?.profilePicture)
                    }

                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.detailFragment, bundle)
                }

                postIconCommentsUserOwnTweet.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("id", id)
                        putString("userId", author?.id)
                        putString("updatedAt", updatedAt)
                        putString("createdAt", createdAt)
                        putString("likes", likes.toString())
                        putBoolean("activateEditText", true)
                        putString("post_description", content)
                        putString("username", author?.username)
                        putString("fullname", author?.fullName)
                        putString("userAvatar", author?.profilePicture)
                    }
                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.detailFragment, bundle)
                }

                postIconHeartUserOwnTweet.setOnClickListener { Snackbar.make(userOwnRootLayout, R.string.in_progress, Snackbar.LENGTH_SHORT).show() }

                postIconShareUserOwnTweet.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    val link = baseUrl+"/"+item.id

                    intent.putExtra(Intent.EXTRA_TEXT, link)
                    intent.type = "text/plain"

                    context.startActivity(Intent.createChooser(intent,"Choose app:"))
                }

                moreSettingsUserOwnTweet.setOnClickListener {

                    val popupMenu = PopupMenu(context, moreSettingsUserOwnTweet)

                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {

                            R.id.edit_user_own_tweet -> {
                                val bundle = Bundle()
                                bundle.putString("description", content)
                                val navController = Navigation.findNavController(holder.itemView)
                                navController.navigate(R.id.editTweetFragment, bundle)
                                true
                            }

                            R.id.delete_user_own_tweet -> {

                                val alertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                                alertDialogBuilder.setTitle(context.resources.getString(R.string.delete_tweet_title))
                                alertDialogBuilder.setMessage(context.resources.getString(R.string.delete_tweet_message))
                                alertDialogBuilder.setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                                    tweetViewModel.deleteTweet(/** this id is tweetId */ id,"Bearer ${SessionManager(context).getToken()}")
                                }

                                alertDialogBuilder.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
                                    dialog.dismiss()
                                }


                                alertDialogBuilder.create().show()

                                tweetViewModel.deleteTweetResult.observe(parentLifecycleOwner) { result ->

                                    when(result){
                                        is TweetDelete.Success -> {
                                            alertDialogBuilder.create().dismiss()
                                            Snackbar.make(userOwnRootLayout, R.string.deleted, Snackbar.LENGTH_SHORT).show()
                                        }
                                        is  TweetDelete.Error -> {
                                            alertDialogBuilder.create().dismiss()
                                            Snackbar.make(userOwnRootLayout, result.message, Snackbar.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.inflate(R.menu.popup_user_menu)

                    popupMenu.converter(popupMenu)
                }

                // TODO { WRITE CODE HERE }

            }
        }


//        val token = sessionManager.getToken()
//
//        viewModel = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner)[LikeViewModel::class.java]
//
//        holder.binding.postIconHeart.setOnClickListener {
//            if (!token.isNullOrEmpty()) {
//                if (isLiked) {
//                    likeCount = likeCount!! - 1
//                    viewModel.likeDelete(likeCount.toString(), item.id, token)
//                    holder.binding.postIconHeart.isSelected = isLiked
//                    holder.binding.postHeartCounter.text = likeCount.toString()
//                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart)
//                } else {
//                    likeCount = likeCount!! + 1
//                    viewModel.likePost(likeCount.toString(),item.id, token)
//                    holder.binding.postIconHeart.isSelected = isLiked
//                    holder.binding.postHeartCounter.text = likeCount.toString()
//                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart_solid_icon)
//                }
//            } else {
//                Toast.makeText(holder.itemView.context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Like
//        viewModel.likePostResult.observe(parentLifecycleOwner) { result ->
//            when (result) {
//                is PostLikeResult.Success -> {  }
//
//                is PostLikeResult.Error -> {
//                    val errorMessage = result.message
//                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        // Delete
//        viewModel.likeDeleteResult.observe(parentLifecycleOwner) { result ->
//            when (result) {
//                is PostLikeResult.Success -> {
//                    Toast.makeText(holder.itemView.context, "Success", Toast.LENGTH_LONG).show()
//                }
//
//                is PostLikeResult.Error -> {
//                    val errorMessage = result.message
//                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}