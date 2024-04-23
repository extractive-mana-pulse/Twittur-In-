package com.example.twitturin.follow.presentation.following.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewFollowingBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.following.sealed.UnFollow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class FollowingAdapter @Inject constructor(
    private val lifecycleOwner : LifecycleOwner,
    private val followViewModel: FollowViewModel
) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowingBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<FollowUser>(){

        override fun areItemsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_following, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val context = holder.itemView.context
        val token = SessionManager(context).getToken()

        item.apply {
            holder.binding.apply {

                val profileImage = profilePicture

                Glide.with(context)
                    .load(profileImage)
                    .error(R.drawable.not_found)
                    .into(userFollowingAvatar)

                fullNameFollowingTv.text = fullName ?: "Twittur User"
                usernameFollowingTv.text = "@$username"
                userFollowingPostDescription.text = bio ?: "This user does not appear to have any biography."

                unfollowBtn.setOnClickListener {
                    followViewModel.unFollowUser(id!!,"Bearer $token")
                }
                holder.itemView.setOnClickListener {
                    Toast.makeText(context, context.resources.getString(R.string.in_progress), Toast.LENGTH_SHORT).show()
                }
            }
        }

        followViewModel.unFollow.observe(lifecycleOwner) { result ->

            when (result) {

                is UnFollow.Success -> {
                    lifecycleOwner.lifecycleScope.launchWhenStarted {
                        followViewModel.sharedFlow.collectLatest {
                            Snackbar.make(holder.itemView, "you unfollow $it", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

                is UnFollow.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}