package com.example.you.adapters.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowPostItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.post.Post
import com.example.you.ui.fragments.dashboard.drawable
import com.example.you.ui.fragments.dashboard.string
import com.google.firebase.auth.FirebaseAuth


class PostPagingAdapter : PagingDataAdapter<Post, PostPagingAdapter.PostViewHolder>(Differ) {

    lateinit var onProfileClick: onProfileClick
    lateinit var onCommentClick: onCommentClick
    lateinit var onViewCommentClick: onViewCommentClick
    lateinit var onLikeClick: onLikeClick
    lateinit var onDeleteClick: onDeleteClick
    lateinit var onLikedByClick: onLikedByClick

    inner class PostViewHolder(val binding: RowPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var post: Post
        fun onBind() {
            post = getItem(absoluteAdapterPosition)!!
            binding.apply {
                tvUserName.text = post.authorUserName
                tvPostText.text = post.text
                ivPostImage.getShapeableImage(post.postImageUrl)
                ivProfile.getShapeableImage(post.authorProfileImageUrl)
                if (post.authorId == FirebaseAuth.getInstance().uid!!) {
                    btnDeletePost.isVisible = true
                    ivProfile.isVisible = false
                    tvUserName.isVisible = false
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

                ivProfile.setOnClickListener {
                    onProfileClick.invoke(post.authorId)
                }
                btnComment.setOnClickListener {
                    onCommentClick.invoke(post.postId)
                }
                btnViewComments.setOnClickListener {
                    onViewCommentClick.invoke(post.postId)
                }
                btnLike.setOnClickListener {
                    if (!post.likeLoading) onLikeClick.invoke(post, absoluteAdapterPosition)
                }
                btnLike.isEnabled = !post.likeLoading

                btnDeletePost.setOnClickListener {
                    onDeleteClick.invoke(post.postId)
                }
                tvLikedBy.setOnClickListener {
                    onLikedByClick.invoke(post.likedBy)
                }
                tvUserName.setOnClickListener {
                    onProfileClick.invoke(post.authorId)
                }
            }
        }
    }

    object Differ : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.onBind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            RowPostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}