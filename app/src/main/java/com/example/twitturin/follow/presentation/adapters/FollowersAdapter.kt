package com.example.twitturin.follow.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.follow.sealed.FollowResult
import com.example.twitturin.follow.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import javax.inject.Inject

class FollowersAdapter @Inject constructor(
    private val lifecycleOwner : LifecycleOwner,
    private val followViewModel: FollowViewModel
) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    private lateinit var sessionManager: SessionManager

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

        val context = holder.itemView.context
        sessionManager = SessionManager(context)
        val token = sessionManager.getToken()

        item.apply {
            holder.binding.apply {
                val profileImage = item.profilePicture

                Glide.with(context)
                    .load(profileImage)
                    .error(R.drawable.not_found)
                    .into(userFollowerAvatar)

                fullNameFollowerTv.text = fullName ?: "Twittur User"
                usernameFollowerTv.text = "@$username"
                postDescription.text = bio ?: "This user does not appear to have any biography."

                followBtn.setOnClickListener {
                    followViewModel.followUsers(id!!,"Bearer $token")
                }
                /** write code here ! */
                // . . .
                /** write code here ! */
            }
        }

        followViewModel.followResult.observe(lifecycleOwner) { result ->

            when (result) {

                is FollowResult.Success -> {
                    Toast.makeText(context, "you follow ${result.user.username}", Toast.LENGTH_SHORT).show()
                }

                is FollowResult.Error -> {  }
            }
        }

        holder.itemView.setOnClickListener {  }
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