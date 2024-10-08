package com.example.twitturin.follow.presentation.followers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.follow.domain.model.FollowUser

class FollowersAdapter(private val clickEvent: (FollowersClickEvent, FollowUser) -> Unit) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    enum class FollowersClickEvent {
        FOLLOW,
        ITEM_SELECTED,
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowersBinding.bind(itemView)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_followers, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val context = holder.itemView.context

        item.apply {
            holder.binding.apply {

                usernameFollowerTv.text = "@$username"
                userFollowerAvatar.loadImagesWithGlideExt(profilePicture)
                fullNameFollowerTv.text = fullName ?: context.resources.getString(R.string.default_user_fullname)
                postDescription.text = bio ?: context.resources.getString(R.string.empty_bio)

                followBtn.setOnClickListener { clickEvent(FollowersClickEvent.FOLLOW, item) }
                holder.itemView.setOnClickListener { clickEvent(FollowersClickEvent.ITEM_SELECTED, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}