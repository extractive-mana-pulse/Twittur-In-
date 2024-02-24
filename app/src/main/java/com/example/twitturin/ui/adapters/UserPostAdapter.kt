package com.example.twitturin.ui.adapters

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewUserTweetsBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserPostAdapter @Inject constructor(
    private val profileViewModel: ProfileViewModel,
    private val parentLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<UserPostAdapter.ViewHolder>() {

    private var list = emptyList<Tweet>()
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var snackbarHelper: SnackbarHelper

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewUserTweetsBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_user_tweets, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DiscouragedPrivateApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context
        val baseUrl = "https://twitturin.onrender.com/tweets"

//        var likeCount: Int? = item.likes
//        var isLiked: Boolean = false

        sessionManager = SessionManager(context)
        snackbarHelper = SnackbarHelper(context.resources)

        holder.binding.apply {
            item.apply {
                val profileImage = "${author?.profilePicture}"

                Glide.with(context)
                    .load(profileImage)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(userAvatarOwnTweet)

                fullNameTvUserOwnTweet.text = author?.fullName ?: "Twittur User"
                usernameTvUserOwnTweet.text = "@" + author?.username
                postDescriptionUserOwnTweet.text = content
                postCommentsCounter.text = replyCount.toString()
                postHeartCounter.text = likes.toString()


                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                try {
                    val date = dateFormat.parse(createdAt)
                    val currentTime = System.currentTimeMillis()
                    val durationMillis = currentTime - date!!.time

                    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
                    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
                    val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
                    val weeks = days / 7

                    val durationString = when {
                        weeks > 0 -> "$weeks w."
                        days > 0 -> "$days d."
                        hours > 0 -> "$hours h."
                        minutes > 0 -> "$minutes m."
                        else -> "$seconds s."
                    }
                    println("Post created $durationString")
                    createdAtTv.text = durationString

                } catch (e: Exception) {
                    println("Invalid date")
                    createdAtTv.text = "Invalid date"
                }

                holder.itemView.setOnClickListener {

                    val bundle = Bundle().apply {
                        putString("fullname", author?.fullName ?: "Twittur User")
                        putString("username", author?.username)
                        putString("post_description", content)
                        putString("createdAt", createdAt)
                        putString("updatedAt", updatedAt)
                        putString("likes", likes.toString())
                        putString("id", id)
                        putString("userId", author?.id)
                        putString("userAvatar", author?.profilePicture)
                    }

                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.detailFragment, bundle)
                }

                postIconCommentsUserOwnTweet.setOnClickListener {

                    val bundle = Bundle().apply {
                        putString("fullname", author?.fullName ?: "Twittur User")
                        putString("username", author?.username)
                        putString("post_description", content)
                        putString("createdAt", createdAt)
                        putString("updatedAt", updatedAt)
                        putString("likes", likes.toString())
                        putString("id", id)
                        putString("userId", author?.id)
                        putString("userAvatar", author?.profilePicture)
                        putBoolean("activateEditText", true)
                    }

                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.detailFragment, bundle)
                }

                postIconHeartUserOwnTweet.setOnClickListener {
                    snackbarHelper.snackbar(
                        holder.itemView.findViewById(R.id.test_user_tv),
                        holder.itemView.findViewById(R.id.test_user_tv),
                        message = context.resources.getString(R.string.in_progress)
                    )
                }

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
                                val navController = Navigation.findNavController(holder.itemView)
                                navController.navigate(R.id.editTweetFragment)
                                true
                            }

                            R.id.delete_user_own_tweet -> {

                                val alertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                                alertDialogBuilder.setTitle(context.resources.getString(R.string.delete_tweet_title))
                                alertDialogBuilder.setMessage(context.resources.getString(R.string.delete_tweet_message))
                                alertDialogBuilder.setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                                    val token = sessionManager.getToken()
                                    profileViewModel.deleteTweet(/** this id is tweetId */ id,"Bearer $token")
                                }

                                alertDialogBuilder.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
                                    dialog.dismiss()
                                }

                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()

                                profileViewModel.deleteTweetResult.observe(parentLifecycleOwner) { result ->

                                    when(result){
                                        is DeleteResult.Success -> {
                                            alertDialog.dismiss()
                                            snackbarHelper.snackbar(
                                                holder.itemView.findViewById(R.id.user_own_root_layout),
                                                holder.itemView.findViewById(R.id.user_own_root_layout),
                                                message = context.resources.getString(R.string.deleted)
                                            )
                                        }
                                        is  DeleteResult.Error -> {
                                            alertDialog.dismiss()
                                            snackbarHelper.snackbarError(
                                                holder.itemView.findViewById(R.id.user_own_root_layout),
                                                holder.itemView.findViewById(R.id.user_own_root_layout),
                                                result.message,
                                                ""){}
                                        }
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.inflate(R.menu.popup_user_menu)

                    try {
                        val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                        fieldMPopup.isAccessible = true
                        val mPopup = fieldMPopup.get(popupMenu)
                        mPopup.javaClass
                            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(mPopup, true)
                    } catch (e: Exception){
                        Log.e("Main", "Error showing menu icons.", e)
                    } finally {
                        popupMenu.show()
                    }
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

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<Tweet>){
        list = newList
        notifyDataSetChanged()
    }
}