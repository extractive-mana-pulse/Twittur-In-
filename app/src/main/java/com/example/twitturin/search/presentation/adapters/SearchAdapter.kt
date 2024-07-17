package com.example.twitturin.search.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewSearchBinding
import com.example.twitturin.search.domain.model.SearchUser

class SearchAdapter(private val searchCLickEvents:(SearchCLickEvents, SearchUser) -> Unit ): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    enum class SearchCLickEvents{
        FOLLOW,
        ITEM
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewSearchBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<SearchUser>(){

        override fun areItemsTheSame(oldItem: SearchUser, newItem: SearchUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchUser, newItem: SearchUser): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_search, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val context = holder.itemView.context

        holder.binding.apply {
            item.apply {

                Glide.with(context)
                    .load(profilePicture)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(searchUserAvatar)

                searchFullNameTv.text = fullName
                searchUsernameTv.text = "@$username"
                searchPostDescriptionTv.text = bio

                holder.itemView.setOnClickListener { searchCLickEvents(SearchCLickEvents.ITEM, item) }

                searchFollowBtn.setOnClickListener { searchCLickEvents(SearchCLickEvents.FOLLOW, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}