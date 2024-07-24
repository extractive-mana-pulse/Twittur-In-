package com.example.twitturin.search.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
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

        holder.binding.apply {
            item.apply {

                searchFullNameTv.text = fullName
                searchPostDescriptionTv.text = bio
                searchUsernameTv.text = "@$username"
                searchUserAvatar.loadImagesWithGlideExt(profilePicture)

                holder.itemView.setOnClickListener { searchCLickEvents(SearchCLickEvents.ITEM, item) }

                searchFollowBtn.setOnClickListener { searchCLickEvents(SearchCLickEvents.FOLLOW, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}