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
import com.example.twitturin.databinding.RcViewFollowersBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import java.util.*

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

        holder.binding.followBtn.setOnClickListener {
            followViewModel.followUsers(item.id!!,"Bearer $token")
        }

        followViewModel.followResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is FollowResult.Success -> {
                    val error = "now you follow: ${item.username?.uppercase()}"
                    val rootView = holder.itemView.findViewById<LinearLayout>(R.id.followers_root_layout)
                    val duration = Snackbar.LENGTH_SHORT

                    val snackbar = Snackbar
                        .make(rootView!!, error, duration)
                        .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_primary))
                        .setTextColor(context.resources.getColor(R.color.md_theme_light_onPrimaryContainer))
                    snackbar.show()
                }
                is FollowResult.Error -> {
                    val error = result.message
                    val rootView = holder.itemView.findViewById<LinearLayout>(R.id.followers_root_layout)
                    val duration = Snackbar.LENGTH_SHORT

                    val snackbar = Snackbar
                        .make(rootView!!, error, duration)
                        .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_errorContainer))
                        .setTextColor(context.resources.getColor(R.color.md_theme_light_onErrorContainer))
                        .setActionTextColor(context.resources.getColor(R.color.md_theme_light_onErrorContainer))
                    snackbar.show()
                }
            }
        }

        holder.itemView.setOnClickListener {
            val error = "In Progress"
            val rootView = holder.itemView.findViewById<LinearLayout>(R.id.followers_root_layout)
            val duration = Snackbar.LENGTH_SHORT

            val snackbar = Snackbar
                .make(rootView!!, error, duration)
                .setBackgroundTint(context.resources.getColor(R.color.md_theme_light_primary))
                .setTextColor(context.resources.getColor(R.color.md_theme_light_onPrimaryContainer))
            snackbar.show()
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