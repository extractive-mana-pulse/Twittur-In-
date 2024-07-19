package com.example.twitturin.home.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.core.extensions.formatCreatedAtPost
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.tweet.domain.model.Tweet
import io.noties.markwon.Markwon

class HomeAdapter(private val homeClickEvents: (HomeClickEvents, Tweet) -> Unit) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    enum class HomeClickEvents {
        ITEM,
        HEART,
        SHARE,
        REPLY,
    }

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
        val context = holder.itemView.context

        holder.binding.apply {
            item.apply {

                Glide.with(context)
                    .load(author?.profilePicture)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(userAvatar)

                usernameTv.text = "@" + author?.username
                postHeartCounter.text = likes.toString()
                postCommentsCounter.text = replyCount.toString()
                createdAtTv.text = createdAt.formatCreatedAtPost()
                fullNameTv.text = author?.fullName ?: R.string.default_user_fullname.toString()
                Markwon.create(context).setMarkdown(postDescription, content)

                holder.itemView.setOnClickListener { homeClickEvents(HomeClickEvents.ITEM, item) }

                postIconHeart.setOnClickListener { homeClickEvents(HomeClickEvents.HEART, item) }

                postIconShare.setOnClickListener { homeClickEvents(HomeClickEvents.SHARE, item) }

                postIconComments.setOnClickListener { homeClickEvents(HomeClickEvents.REPLY, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}