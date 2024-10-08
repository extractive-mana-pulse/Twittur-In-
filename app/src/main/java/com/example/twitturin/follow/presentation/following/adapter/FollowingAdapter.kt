package com.example.twitturin.follow.presentation.following.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.databinding.RcViewFollowingBinding
import com.example.twitturin.follow.domain.model.FollowUser

class FollowingAdapter(private val clickEvent: (FollowingClickEvent, FollowUser) -> Unit) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    enum class FollowingClickEvent {
        UNFOLLOW,
        ITEM,
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowingBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<FollowUser>(){

        override fun areItemsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_following, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val context = holder.itemView.context

        item.apply {
            holder.binding.apply {

                usernameFollowingTv.text = "@$username"
                userFollowingAvatar.loadImagesWithGlideExt(profilePicture)
                fullNameFollowingTv.text = fullName ?: context.resources.getString(R.string.default_user_fullname)
                userFollowingPostDescription.text = bio ?: context.resources.getString(R.string.empty_bio)

                unfollowBtn.setOnClickListener { clickEvent(FollowingClickEvent.UNFOLLOW, item) }
                holder.itemView.setOnClickListener { clickEvent(FollowingClickEvent.ITEM, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}