package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.google.android.material.snackbar.Snackbar

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

        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()
        followViewModel = ViewModelProvider(context as ViewModelStoreOwner)[FollowUserViewModel::class.java]

        holder.binding.unfollowBtn.setOnClickListener {
            followViewModel.deleteFollow(item.id!!,"Bearer $token")
        }

        followViewModel.deleteFollowResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is DeleteFollow.Success -> {
                    val error = "you unfollow: ${item.username?.uppercase()}"
                    val rootView = holder.itemView.findViewById<LinearLayout>(R.id.following_root_layout)
                    val duration = Snackbar.LENGTH_SHORT

                    val snackbar = Snackbar
                        .make(rootView, error, duration)
                        .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_primary))
                        .setTextColor(context.resources.getColor(R.color.md_theme_light_onPrimaryContainer))
                    snackbar.show()
                }
                is DeleteFollow.Error -> {

                    val error = result.message
                    val rootView = holder.itemView.findViewById<LinearLayout>(R.id.following_root_layout)
                    val duration = Snackbar.LENGTH_SHORT

                    val snackbar = Snackbar
                        .make(rootView, error, duration)
                        .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_errorContainer))
                        .setTextColor(context.resources.getColor(R.color.md_theme_light_onErrorContainer))
                        .setActionTextColor(context.resources.getColor(R.color.md_theme_light_onErrorContainer))
                    snackbar.show()
                }
            }
        }

        holder.itemView.setOnClickListener {
            val error = "In Progress"
            val rootView = holder.itemView.findViewById<LinearLayout>(R.id.following_root_layout)
            val duration = Snackbar.LENGTH_SHORT

            val snackbar = Snackbar
                .make(rootView!!, error, duration)
                .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_primary))
                .setTextColor(context.resources.getColor(R.color.md_theme_light_onPrimaryContainer))
            snackbar.show()
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