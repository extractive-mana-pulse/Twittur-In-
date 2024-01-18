package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import java.util.*
import javax.inject.Inject

class FollowersAdapter (
    private val parentLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
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

        val context = holder.itemView.context
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
        followViewModel = ViewModelProvider(context as ViewModelStoreOwner)[FollowUserViewModel::class.java]

        holder.binding.followBtn.setOnClickListener {
            followViewModel.followUsers(item.id!!,"Bearer $token")
        }

        followViewModel.followResult.observe(parentLifecycleOwner) { result ->

            when (result) {

                is FollowResult.Success -> {
                    snackbarHelper.snackbar(
                        holder.itemView.findViewById(R.id.followers_root_layout),
                        holder.itemView.findViewById(R.id.followers_root_layout),
                        message = "now you follow: ${item.username?.uppercase()}"
                    )
                }

                is FollowResult.Error -> {
                    snackbarHelper.snackbarError(
                        holder.itemView.findViewById(R.id.followers_root_layout),
                        holder.itemView.findViewById(R.id.followers_root_layout),
                        result.message,
                        ""){}
                }
            }
        }

        holder.itemView.setOnClickListener {
            snackbarHelper.snackbar(
                holder.itemView.findViewById(R.id.followers_root_layout),
                holder.itemView.findViewById(R.id.followers_root_layout),
                message = "In Progress"
            )
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