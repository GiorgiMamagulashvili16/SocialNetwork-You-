package com.example.you.adapters.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowChatLeftItemBinding
import com.example.you.databinding.RowChatRightItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.message.Message
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MESSAGE_TYPE_OTHER_USER = 0
        private const val MESSAGE_TYPE_CURRENT_USER = 1
    }

    private val util = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, util)

    inner class OtherUserViewHolder(val binding: RowChatLeftItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var message: Message
        fun onBind() {
            message = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                tvChatText.text = message.message
                ivProfileImage.getShapeableImage(message.senderProfileImage)
                tvUserName.text = message.senderUserName
            }
        }
    }

    inner class CurrentUserViewHolder(val binding: RowChatRightItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var message: Message
        fun onBind() {
            message = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                tvChatText.text = message.message
                ivProfileImage.getShapeableImage(message.senderProfileImage)
                tvUserName.text = message.senderUserName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_CURRENT_USER) {
            CurrentUserViewHolder(
                RowChatRightItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OtherUserViewHolder(
                RowChatLeftItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is OtherUserViewHolder -> holder.onBind()
        is CurrentUserViewHolder -> holder.onBind()
        else -> Unit
    }

    override fun getItemCount(): Int = differ.currentList.size
    override fun getItemViewType(position: Int): Int {
        val message = differ.currentList[position]
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid!!
        return if (message.senderId == currentUser)
            MESSAGE_TYPE_CURRENT_USER
        else
            MESSAGE_TYPE_OTHER_USER
    }
}