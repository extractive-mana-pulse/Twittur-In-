package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
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

class FollowersAdapter @Inject constructor(
    private val lifecycleOwner : LifecycleOwner,
    private val followViewModel: FollowUserViewModel
) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private var list = emptyList<User>()
    private lateinit var sessionManager: SessionManager
//    private lateinit var snackbarHelper: SnackbarHelper

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
//        snackbarHelper = SnackbarHelper(context.resources)

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

        holder.binding.followBtn.setOnClickListener {
            followViewModel.followUsers(item.id!!,"Bearer $token")
        }

        followViewModel.followResult.observe(lifecycleOwner) { result ->

            when (result) {

                is FollowResult.Success -> {
                    Toast.makeText(context, "now you follow: ${item.username?.uppercase()}", Toast.LENGTH_SHORT).show()
//                    snackbarHelper.snackbar(
//                        holder.itemView.findViewById(R.id.followers_rc_root_layout),
//                        holder.itemView.findViewById(R.id.anchor_rc_followers_tv),
//                        message = "now you follow: ${item.username?.uppercase()}"
//                    )
                }

                is FollowResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
//                    snackbarHelper.snackbarError(
//                        holder.itemView.findViewById(R.id.followers_rc_root_layout),
//                        holder.itemView.findViewById(R.id.anchor_rc_followers_tv),
//                        result.message,
//                        ""){}
                }
            }
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "In Progress", Toast.LENGTH_SHORT).show()
//            snackbarHelper.snackbar(
//                holder.itemView.findViewById(R.id.followers_root_layout),
//                holder.itemView.findViewById(R.id.anchor_rc_followers_tv),
//                message = "In Progress"
//            )
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