package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.vm.LikeViewModel
import com.example.twitturin.manager.SessionManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PostAdapter @Inject constructor(
    private val likeViewModel: LikeViewModel,
    private val parentLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var list = emptyList<Tweet>()
    @Inject lateinit var sessionManager: SessionManager
    private lateinit var snackbarHelper: SnackbarHelper

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val baseTweetsUrl = "https://twitturin.onrender.com/tweets"
        val context = holder.itemView.context

        snackbarHelper = SnackbarHelper(context.resources)

//        var likeCount: Int? = item.likes
//        var isLiked: Boolean = false

        holder.binding.apply {
            item.apply {

                val profileImage = "${author?.profilePicture}"

                Glide.with(context)
                    .load(profileImage)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(userAvatar)

                fullNameTv.text = author?.fullName ?: "Twittur User"
                usernameTv.text = "@" + author?.username
                postDescription.text = HtmlCompat.fromHtml(postDescription.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
                postDescription.text = content
                postCommentsCounter.text = replyCount.toString()
                postHeartCounter.text = likes.toString()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                try {
                    val date = dateFormat.parse(item.createdAt)
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
                    createdAtTv.text = durationString

                } catch (e: Exception) {
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

                postIconComments.setOnClickListener {

                    val bundle = Bundle().apply {
                        putString("fullname", author?.fullName ?: "Twittur User")
                        putString("username", author?.username)
                        putString("post_description", content)
                        putString("createdAt", createdAt)
                        putString("updatedAt", updatedAt)
                        putString("likes", likes.toString())
                        putString("id",id)
                        putString("userId",author?.id)
                        putString("userAvatar", author?.profilePicture)
                        putBoolean("activateEditText", true)
                    }

                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.detailFragment, bundle)
                }

                postIconHeart.setOnClickListener {
                    Toast.makeText(context, context.resources.getString(R.string.in_progress), Toast.LENGTH_SHORT).show()
                }

                postIconShare.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    val link = "$baseTweetsUrl/$id"

                    intent.putExtra(Intent.EXTRA_TEXT, link)
                    intent.type = "text/plain"

                    context.startActivity(Intent.createChooser(intent,"Choose app:"))
                }
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