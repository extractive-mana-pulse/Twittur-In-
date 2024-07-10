package com.example.twitturin.follow.presentation.followers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.followers.sealed.FollowersUiEvent
import com.example.twitturin.follow.presentation.followers.vm.FollowersUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowersAdapter @Inject constructor(
    private val lifecycleOwner : LifecycleOwner,
    private val followViewModel: FollowViewModel,
    private val followersUiViewModel : FollowersUiViewModel
) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewFollowersBinding.bind(itemView)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_followers, parent, false)
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
                    .into(userFollowerAvatar)

                usernameFollowerTv.text = "@$username"
                postDescription.text = bio ?: R.string.empty_bio.toString()
                fullNameFollowerTv.text = fullName ?: R.string.default_user_fullname.toString()

                followBtn.setOnClickListener { followViewModel.followUsers(id!!,"Bearer $token") }

                holder.itemView.setOnClickListener { followersUiViewModel.sendUiEvent(FollowersUiEvent.OnItemPressed) }

                lifecycleOwner.lifecycleScope.launch {
                    followersUiViewModel.followersEvent.collect {event ->
                        when (event) {
                            is FollowersUiEvent.OnItemPressed -> {
                                Navigation.findNavController(holder.itemView).navigate(R.id.observeProfileFragment)
                            }
                            is FollowersUiEvent.OnFollowPressed -> {

                            }
                            is FollowersUiEvent.NothingState -> {  }
                        }
                    }
                }
            }
        }

        followViewModel.follow.observe(lifecycleOwner) { result ->

            when (result) {

                is Follow.Success -> {
                    lifecycleOwner.lifecycleScope.launchWhenStarted {
                        followViewModel.sharedFlow.collectLatest {
                            Snackbar.make(holder.itemView, "you follow $it",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

                is Follow.Error -> {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}