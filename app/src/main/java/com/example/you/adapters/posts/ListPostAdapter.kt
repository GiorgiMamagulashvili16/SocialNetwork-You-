package com.example.you.adapters.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowListPostItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.post.Post

typealias onDeleteClick = (postId: String) -> Unit

class ListPostAdapter : RecyclerView.Adapter<ListPostAdapter.PostViewHolder>() {
    lateinit var onDeleteClick: onDeleteClick
    lateinit var onViewCommentClick: onViewCommentClick

    inner class PostViewHolder(val binding: RowListPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var post: Post
        fun onBind() {
            post = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                tvPostText.text = post.text
                ivPostImage.getShapeableImage(post.postImageUrl)
                btnDeletePost.setOnClickListener {
                    onDeleteClick.invoke(post.postId)
                }
                btnViewComments.setOnClickListener {
                    onViewCommentClick.invoke(post.postId)
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
            RowListPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.onBind()

    override fun getItemCount(): Int = differ.currentList.size


}