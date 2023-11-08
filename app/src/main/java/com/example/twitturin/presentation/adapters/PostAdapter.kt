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
        holder.binding.apply {
            userNickname.text = item.author.username
            postDescription.text = item.content
            postHeartCounter.text = item.likes.toString()
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