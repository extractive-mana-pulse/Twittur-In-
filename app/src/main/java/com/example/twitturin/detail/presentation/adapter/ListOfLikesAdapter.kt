package com.example.twitturin.detail.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.databinding.ListOfLikesLayoutBinding
import com.example.twitturin.detail.domain.model.UserLikesAPost
import io.noties.markwon.Markwon

class ListOfLikesAdapter : RecyclerView.Adapter<ListOfLikesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListOfLikesLayoutBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<UserLikesAPost>(){

        override fun areItemsTheSame(oldItem: UserLikesAPost, newItem: UserLikesAPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserLikesAPost, newItem: UserLikesAPost): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_of_likes_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val context = holder.itemView.context

        holder.binding.apply {
            item.apply {

                usernameLolTv.text = "@$username"
                userLolAvatar.loadImagesWithGlideExt(profilePicture)
                fullNameLolTv.text = fullName ?: R.string.default_user_fullname.toString()
                Markwon.create(context).setMarkdown(postDescription, bio.toString())
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}