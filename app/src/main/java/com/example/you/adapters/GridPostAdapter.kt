package com.example.you.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowGridImageItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.post.Post

class GridPostAdapter : RecyclerView.Adapter<GridPostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: RowGridImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var post: Post
        fun onBind() {
            post = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                tvGridImage.getShapeableImage(post.postImageUrl)
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
            RowGridImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.onBind()

    override fun getItemCount(): Int = differ.currentList.size


}