package com.example.twitturin.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.RcViewBinding
import com.example.twitturin.model.data.tweets.ApiTweetsItem
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.activities.DetailActivity
import com.example.twitturin.ui.sealeds.PostLikeResult
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.ViewModelFactory3
import java.text.SimpleDateFormat
import java.util.*


class PostAdapter(private val parentLifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var list = emptyList<ApiTweetsItem>()

    private lateinit var viewModel: LikeViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        var likeCount: Int = item.likes
        var isLiked: Boolean = false

        holder.binding.apply {
            fullNameTv.text = item.author.fullName
            usernameTv.text = item.author.username
            postDescription.text = item.content
            postCommentsCounter.text = item.replyCount.toString()
            postHeartCounter.text = item.likes.toString()
            createdAtTv.text = item.createdAt
//            val time = item.createdAt
//            calculateTimeAgo(time)
//            createdAtTv.text = time

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("fullname", item.author.fullName)
            intent.putExtra("username", item.author.username)
            intent.putExtra("post_description", item.content)
            intent.putExtra("link", item.likedBy.firstOrNull()?.fullName)
            intent.putExtra("createdAt", item.createdAt)
            holder.itemView.context.startActivity(intent)
        }

        val sessionManager = SessionManager(holder.itemView.context)

        val token = sessionManager.getToken()
        val repository = Repository()
        val viewModelFactory = ViewModelFactory3(repository)
        val viewModelProvider = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner, viewModelFactory)
        val viewModel = viewModelProvider[LikeViewModel::class.java]

        holder.binding.postIconHeart.setOnClickListener {
            if (!token.isNullOrEmpty()) {
                if (isLiked) {
                    likeCount--
                    isLiked = !isLiked
                    viewModel.likePost(likeCount.toString(), token)
                    holder.binding.postIconHeart.isSelected = isLiked
                    holder.binding.postHeartCounter.text = likeCount.toString()
                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart)
                } else {
                    likeCount++
                    isLiked = !isLiked
                    viewModel.likePost(likeCount.toString(), token)
                    holder.binding.postIconHeart.isSelected = isLiked
                    holder.binding.postHeartCounter.text = likeCount.toString()
                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart_solid_icon)
                }
            } else {
                Toast.makeText(holder.itemView.context, "something went wrong my G", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.likePostResult.observe(parentLifecycleOwner) { result ->
            when (result) {
                is PostLikeResult.Success -> {
                    Toast.makeText(holder.itemView.context, "Success", Toast.LENGTH_LONG).show()
                }

                is PostLikeResult.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun calculateTimeAgo(postCreationTime: String): String {
        val serverTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val postDate = serverTimeFormat.parse(postCreationTime)

        val currentDate = Date()

        return DateUtils.getRelativeTimeSpanString(postDate!!.time, currentDate.time, DateUtils.MINUTE_IN_MILLIS).toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<ApiTweetsItem>){
        list = newList
        notifyDataSetChanged()
    }
}