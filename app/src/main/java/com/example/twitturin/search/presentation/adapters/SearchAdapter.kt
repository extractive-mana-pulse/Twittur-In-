package com.example.twitturin.search.presentation.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.databinding.RcViewSearchBinding
import com.example.twitturin.manager.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class SearchAdapter @Inject constructor() : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    @Inject lateinit var sessionManager: SessionManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewSearchBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_search, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        holder.binding.apply {
            item.apply {
                val profileImage = "$profilePicture"

                Glide.with(context)
                    .load(profileImage)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(searchUserAvatar)

                searchFullNameTv.text = fullName ?: "Twittur User"
                searchUsernameTv.text = "@$username"

                holder.itemView.setOnClickListener {

                    val bundle = Bundle().apply {
                        putString("fullname", fullName ?: "Twittur User")
                        putString("username", username)
                        putString("id",id)
                        putBoolean("activateEditText", true)
                    }

                    val navController = Navigation.findNavController(holder.itemView)
                    navController.navigate(R.id.observeProfileFragment, bundle)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<User>){
        list = newList
        notifyDataSetChanged()
    }
}