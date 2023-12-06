package com.example.twitturin.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User

class TweetsAdapter(var posts: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {

        val post = posts[position]

        holder.binding.apply {
            fullNameTv.text = post.author!!.fullName
            usernameTv.text = post.author!!.username
            postDescription.text = post.content
        }
    }

    override fun getItemCount() = posts.size
}