package com.example.twitturin.tweet.presentation.home.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.detail.ui.util.formatCreatedAt
import com.example.twitturin.tweet.presentation.home.ui.util.formatCreatedAtPost
import javax.inject.Inject

class PostAdapter @Inject constructor() : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val baseTweetsUrl = "https://twitturin.onrender.com/tweets"
        val context = holder.itemView.context

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

                createdAtTv.text = createdAt.formatCreatedAtPost()

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
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}