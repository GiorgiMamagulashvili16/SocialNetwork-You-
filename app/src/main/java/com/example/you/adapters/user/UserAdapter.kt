package com.example.you.adapters.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowUserItemBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.user.UserModel

typealias onUserClick = (uid: String) -> Unit

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    lateinit var onUserClick: onUserClick

    inner class UserViewHolder(val binding: RowUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var user: UserModel
        fun onBind() {
            val user = differ.currentList[absoluteAdapterPosition]
            binding.apply {
                ivProfileImage.getShapeableImage(user.profileImageUrl)
                tvUserName.text = user.userName
                root.setOnClickListener {
                    onUserClick.invoke(user.uid)
                }
            }
        }
    }

    private val util = object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, util)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            RowUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.onBind()

    override fun getItemCount(): Int = differ.currentList.size
}