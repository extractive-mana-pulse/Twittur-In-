package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.activities.DetailActivity
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.ui.sealeds.PostLikeResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.LikeViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FollowersAdapter(private val parentLifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    private lateinit var followViewModel: FollowUserViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowersBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_followers, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {

            val profileImage = item.profilePicture

            Glide.with(holder.itemView.context)
                .load(profileImage)
                .error(R.drawable.not_found)
                .into(userFollowerAvatar)

            fullNameFollowerTv.text = item.fullName ?: "Twittur User"
            usernameFollowerTv.text = "@" + item.username
            postDescription.text = item.bio ?: "This user does not appear to have any biography."
        }

        val sessionManager = SessionManager(holder.itemView.context)
        val token = sessionManager.getToken()
        followViewModel = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner)[FollowUserViewModel::class.java]

        holder.binding.followBtn.setOnClickListener {
            followViewModel.followUsers(item.id!!,"Bearer $token")
        }

        followViewModel.followResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is FollowResult.Success -> {
                    Toast.makeText(holder.itemView.context, "now you follow: ${item.username}", Toast.LENGTH_SHORT).show()
                }
                is FollowResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "in progress", Toast.LENGTH_SHORT).show()
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