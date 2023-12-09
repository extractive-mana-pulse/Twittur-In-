package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.activities.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class TweetsAdapter(private var posts: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.apply {
            fullNameTv.text = post.author?.fullName
            Log.d("fullname", post.author?.fullName.toString())
            usernameTv.text = "@" + post.author?.username
            postDescription.text = post.content
            postCommentsCounter.text = post.replyCount.toString()
            postHeartCounter.text = post.likes.toString()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            try {
                val date = dateFormat.parse(post.createdAt)
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
                    minutes > 0 -> "$minutes min."
                    else -> "$seconds sec."
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
            intent.putExtra("fullname", post.author?.fullName)
            intent.putExtra("username", post.author?.username)
            intent.putExtra("post_description", post.content)
            intent.putExtra("createdAt", post.createdAt)
            intent.putExtra("userId", post.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = posts.size
}