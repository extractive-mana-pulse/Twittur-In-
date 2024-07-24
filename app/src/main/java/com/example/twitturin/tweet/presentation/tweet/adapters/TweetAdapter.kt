package com.example.twitturin.tweet.presentation.tweet.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitturin.R
import com.example.twitturin.core.extensions.formatCreatedAtPost
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.databinding.RcViewUserTweetsBinding
import com.example.twitturin.tweet.domain.model.Tweet
import io.noties.markwon.Markwon

class TweetAdapter(private val clickEvents: (TweetClickEvents, Tweet) -> Unit) : RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    enum class TweetClickEvents{
        ITEM,
        REPLY,
        HEART,
        MORE,
        SHARE
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RcViewUserTweetsBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Tweet>(){

        override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_user_tweets, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DiscouragedPrivateApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = differ.currentList[position]

        holder.binding.apply {
            item.apply {

                postHeartCounter.text = likes.toString()
                postCommentsCounter.text = replyCount.toString()
                usernameTvUserOwnTweet.text = "@" + author?.username
                createdAtTv.text = createdAt?.formatCreatedAtPost()
                userAvatarOwnTweet.loadImagesWithGlideExt(author?.profilePicture)
                Markwon.create(context).setMarkdown(postDescriptionUserOwnTweet, content!!)
                fullNameTvUserOwnTweet.text = author?.fullName ?: R.string.default_user_fullname.toString()

                holder.itemView.setOnClickListener { clickEvents(TweetClickEvents.ITEM, item) }
                moreSettingsUserOwnTweet.setOnClickListener { clickEvents(TweetClickEvents.MORE, item) }
                postIconHeartUserOwnTweet.setOnClickListener { clickEvents(TweetClickEvents.HEART, item) }
                postIconShareUserOwnTweet.setOnClickListener { clickEvents(TweetClickEvents.SHARE, item) }
                postIconCommentsUserOwnTweet.setOnClickListener { clickEvents(TweetClickEvents.REPLY, item) }
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}

//        var likeCount: Int? = item.likes
//        var isLiked: Boolean = false

//        val token = sessionManager.getToken()
//
//        viewModel = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner)[LikeViewModel::class.java]
//
//        holder.binding.postIconHeart.setOnClickListener {
//            if (!token.isNullOrEmpty()) {
//                if (isLiked) {
//                    likeCount = likeCount!! - 1
//                    viewModel.likeDelete(likeCount.toString(), item.id, token)
//                    holder.binding.postIconHeart.isSelected = isLiked
//                    holder.binding.postHeartCounter.text = likeCount.toString()
//                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart)
//                } else {
//                    likeCount = likeCount!! + 1
//                    viewModel.likePost(likeCount.toString(),item.id, token)
//                    holder.binding.postIconHeart.isSelected = isLiked
//                    holder.binding.postHeartCounter.text = likeCount.toString()
//                    holder.binding.postIconHeart.setBackgroundResource(R.drawable.heart_solid_icon)
//                }
//            } else {
//                Toast.makeText(holder.itemView.context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Like
//        viewModel.likePostResult.observe(parentLifecycleOwner) { result ->
//            when (result) {
//                is PostLikeResult.Success -> {  }
//
//                is PostLikeResult.Error -> {
//                    val errorMessage = result.message
//                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        // Delete
//        viewModel.likeDeleteResult.observe(parentLifecycleOwner) { result ->
//            when (result) {
//                is PostLikeResult.Success -> {
//                    Toast.makeText(holder.itemView.context, "Success", Toast.LENGTH_LONG).show()
//                }
//
//                is PostLikeResult.Error -> {
//                    val errorMessage = result.message
//                    Toast.makeText(holder.itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }