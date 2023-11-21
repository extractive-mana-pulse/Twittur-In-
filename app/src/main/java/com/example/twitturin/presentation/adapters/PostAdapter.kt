package com.example.twitturin.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem


class PostAdapter : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var list = emptyList<ApiTweetsItem>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        var likeCount: Int = item.likes
        var isLiked: Boolean = false

        holder.binding.apply {
            fullNameTv.text = item.author.fullName
            usernameTv.text = item.author.username
            postDescription.text = item.content
            postCommentsCounter.text = item.replyCount.toString()
            postHeartCounter.text = item.likes.toString()
            postIconHeart.isSelected = isLiked
        }

        holder.binding.postIconHeart.setOnClickListener {
            if (isLiked) {
                likeCount--
                holder.binding.postHeartCounter.text = likeCount.toString()
                holder.binding.postIconHeart.isSelected = false
            } else {
                likeCount++
                holder.binding.postHeartCounter.text = likeCount.toString()
                holder.binding.postIconHeart.isSelected = true
            }
            isLiked = !isLiked
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<ApiTweetsItem>){
        list = newList
        notifyDataSetChanged()
    }
}