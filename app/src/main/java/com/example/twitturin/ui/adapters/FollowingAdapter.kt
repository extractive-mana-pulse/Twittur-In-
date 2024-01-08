package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.databinding.RcViewFollowingBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.DeleteFollow
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.manager.SessionManager

class FollowingAdapter(private val parentLifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    private lateinit var followViewModel: FollowUserViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowingBinding.bind(itemView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_following, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: FollowingAdapter.ViewHolder, position: Int) {
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

        holder.binding.unfollowBtn.setOnClickListener {
            followViewModel.deleteFollow(item.id!!,"Bearer $token")
        }

        followViewModel.deleteFollowResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is DeleteFollow.Success -> {
//                    Log.d("unfollow","unfollowing!${item.username}")
//                    Toast.makeText(holder.itemView.context, "you unfollow: ${item.username}", Toast.LENGTH_SHORT).show()
                }
                is DeleteFollow.Error -> {
                    val error = result.message
                    Toast.makeText(holder.itemView.context, error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "in progress", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<User>){
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}