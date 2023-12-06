package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.ui.activities.DetailActivity
import com.example.twitturin.ui.sealeds.PostLikeResult
import com.example.twitturin.viewmodel.LikeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PostAdapter(private val parentLifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var list = emptyList<Tweet>()

    private lateinit var viewModel: LikeViewModel

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
        var likeCount: Int? = item.likes
        var isLiked: Boolean = false

        holder.binding.apply {
            fullNameTv.text = item.author?.fullName
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
                    weeks > 0 -> "$weeks w. ago"
                    days > 0 -> "$days d. ago"
                    hours > 0 -> "$hours h. ago"
                    minutes > 0 -> "$minutes min. ago"
                    else -> "$seconds sec. ago"
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
            intent.putExtra("userId", item.id)
            holder.itemView.context.startActivity(intent)
        }

        val sessionManager = SessionManager(holder.itemView.context)
        val token = sessionManager.getToken()

        viewModel =
            ViewModelProvider(holder.itemView.context as ViewModelStoreOwner)[LikeViewModel::class.java]

        holder.binding.postIconHeart.setOnClickListener {
            if (!token.isNullOrEmpty()) {
                if (isLiked) {
                    likeCount = likeCount!! - 1
                    isLiked = !isLiked
                    viewModel.likePost(likeCount.toString(), token)
                    holder.binding.postIconHeart.isSelected = isLiked
                    holder.binding.postHeartCounter.text = likeCount.toString()
                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart)
                } else {
                    likeCount = likeCount!! + 1
                    isLiked = !isLiked
                    viewModel.likePost(likeCount.toString(), token)
                    holder.binding.postIconHeart.isSelected = isLiked
                    holder.binding.postHeartCounter.text = likeCount.toString()
                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart_solid_icon)
                }
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "something went wrong my G",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.likePostResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is PostLikeResult.Success -> {
                    Toast.makeText(holder.itemView.context, "Success", Toast.LENGTH_LONG).show()
                }

                is PostLikeResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
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