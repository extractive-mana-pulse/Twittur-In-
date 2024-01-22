package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
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
import com.example.twitturin.databinding.RcViewFollowingBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.DeleteFollow
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import javax.inject.Inject

class FollowingAdapter @Inject constructor(
    private val lifecycleOwner : LifecycleOwner,
    private val followViewModel: FollowUserViewModel
) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    private lateinit var sessionManager: SessionManager

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
        val context = holder.itemView.context

        sessionManager = SessionManager(context)

        holder.binding.apply {

            val profileImage = item.profilePicture

            Glide.with(context)
                .load(profileImage)
                .error(R.drawable.not_found)
                .into(userFollowerAvatar)

            fullNameFollowerTv.text = item.fullName ?: "Twittur User"
            usernameFollowerTv.text = "@" + item.username
            postDescription.text = item.bio ?: "This user does not appear to have any biography."
        }

        val token = sessionManager.getToken()

        holder.binding.unfollowBtn.setOnClickListener {
            followViewModel.deleteFollow(item.id!!,"Bearer $token")
        }

        followViewModel.deleteFollowResult.observe(lifecycleOwner) { result ->
            when (result) {

                is DeleteFollow.Success -> {
                    Toast.makeText(context, "you unfollow: ${item.username?.uppercase()}", Toast.LENGTH_SHORT).show()
                }

                is DeleteFollow.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "In Progress", Toast.LENGTH_SHORT).show()
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