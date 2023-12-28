package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.ui.activities.DetailActivity
import com.example.twitturin.ui.sealeds.PostLikeResult
import com.example.twitturin.viewmodel.LikeViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PostAdapter(private val parentLifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var list = emptyList<Tweet>()
//    private lateinit var viewModel: LikeViewModel

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
        val baseUrl = "https://twitturin.onrender.com/tweets"
//        var likeCount: Int? = item.likes
//        var isLiked: Boolean = false

        holder.binding.apply {
            val profileImage = "${item.author?.profilePicture}"

            Glide.with(holder.itemView.context)
                .load(profileImage)
                .error(R.drawable.not_found)
                .centerCrop()
                .into(userAvatar)

            fullNameTv.text = item.author?.fullName ?: "Twittur User"
            usernameTv.text = "@" + item.author?.username
            postDescription.text = item.content
            postCommentsCounter.text = item.replyCount.toString()
            postHeartCounter.text = item.likes.toString()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            try {
                val date = dateFormat.parse(item.createdAt)
                val currentTime = System.currentTimeMillis()
                val durationMillis = currentTime - date.time

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
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)

            intent.putExtra("fullname", item.author?.fullName)
            intent.putExtra("username", item.author?.username)
            intent.putExtra("post_description", item.content)
            intent.putExtra("createdAt", item.createdAt)
            intent.putExtra("likes", item.likes.toString())
            intent.putExtra("id",item.id)
            intent.putExtra("userId",item.author?.id)
            intent.putExtra("userAvatar", item.author?.profilePicture)

            holder.itemView.context.startActivity(intent)
        }

        holder.binding.postIconShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val link = baseUrl+"/"+item.id

            intent.putExtra(Intent.EXTRA_TEXT, link)
            intent.type = "text/plain"

            holder.itemView.context.startActivity(Intent.createChooser(intent,"Choose app:"))
        }

//        val sessionManager = SessionManager(holder.itemView.context)
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