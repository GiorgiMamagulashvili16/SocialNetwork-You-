package com.example.you.adapters.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.R
import com.example.you.databinding.RowOtherUserPostItemBinding
import com.example.you.extensions.getImageFromUrl
import com.example.you.models.post.Post
import com.example.you.ui.fragments.dashboard.drawable
import com.example.you.ui.fragments.dashboard.string

class OtherUserPostAdapter : RecyclerView.Adapter<OtherUserPostAdapter.PostViewHolder>() {

    lateinit var onViewCommentClick: onViewCommentClick
    lateinit var onLikeClick: onLikeClick
    lateinit var onCommentClick: onCommentClick

    inner class PostViewHolder(val binding: RowOtherUserPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var post: Post
        fun onBind() {

            post = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                tvPostText.text = post.text
                ivPostImage.getImageFromUrl(post.postImageUrl)
                btnViewComments.setOnClickListener {
                    onViewCommentClick.invoke(post.postId)
                }
                btnLike.setImageResource(if (post.isLiked) drawable.ic_liked else drawable.ic_heart)
                val likes = post.likedBy.size
                tvLikeTxt.isVisible = likes > 0

                tvLikedBy.text = when {
                    likes <= 0 -> root.context.getString(string.no_like_yet)
                    likes == 1 -> root.context.getString(string.post_liked_list_size, 1, "User")
                    else -> root.context.getString(
                        string.post_liked_list_size,
                        post.likedBy.size,
                        "People"
                    )
                }
                btnLike.setOnClickListener {
                    if (!post.likeLoading) onLikeClick.invoke(post, absoluteAdapterPosition)
                }
                btnComment.setOnClickListener {
                    onCommentClick.invoke(post.postId)
                }
            }
        }
    }

    private val util = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, util)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            RowOtherUserPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int)  = holder.onBind()

    override fun getItemCount(): Int = differ.currentList.size


}