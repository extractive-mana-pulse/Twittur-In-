package com.example.twitturin.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.PostItem
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding

class PostAdapter(private val postList: List<PostItem>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postItem = postList[position]
        holder.binding.apply {
            userAvatar.setImageResource(postItem.userImageId)
            userNickname.text = postItem.name
            postDescription.text = postItem.postDescription
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }
}