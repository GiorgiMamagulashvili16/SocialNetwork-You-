package com.example.you.adapters.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.adapters.posts.onDeleteClick
import com.example.you.databinding.RowCommentItemBinding
import com.example.you.databinding.RowCurrentUserCommentItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.post.Comment
import com.google.firebase.auth.FirebaseAuth

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    lateinit var onDeleteClick: onDeleteClick

    companion object {
        private const val CURRENT_USER_COMMENT = 0
        private const val OTHER_USER_COMMENT = 1
    }

    inner class CommentViewHolder(val binding: RowCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var comment: Comment
        fun onBind() {
            comment = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                ivCommentUserImage.getShapeableImage(comment.authorProfileImage)
                tvCommentText.text = comment.commentText
                tvUserName.text = comment.authorUsername
            }
        }
    }

    inner class CurrentUserCommentViewHolder(val binding: RowCurrentUserCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var comment: Comment
        fun onBind() {
            comment = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                ivCommentUserImage.getShapeableImage(comment.authorProfileImage)
                tvCommentText.text = comment.commentText
                tvUserName.text = comment.authorUsername
                btnDeletePost.setOnClickListener {
                    onDeleteClick.invoke(comment.commentId)
                }
            }
        }
    }

    private val util = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, util)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CURRENT_USER_COMMENT) {
            CurrentUserCommentViewHolder(
                RowCurrentUserCommentItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            CommentViewHolder(
                RowCommentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentUserCommentViewHolder -> holder.onBind()
            is CommentViewHolder -> holder.onBind()
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val comment = differ.currentList[position]
        return if (comment.authorId == auth.currentUser?.uid!!) {
            CURRENT_USER_COMMENT
        } else {
            OTHER_USER_COMMENT
        }
    }
}