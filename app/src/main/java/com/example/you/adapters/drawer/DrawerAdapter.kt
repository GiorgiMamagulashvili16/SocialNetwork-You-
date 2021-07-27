package com.example.you.adapters.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.you.databinding.RowDrawerItemBinding
import com.example.you.databinding.RowDrawerItemUnderlineBinding
import com.example.you.models.drawer.DrawerItem
import com.example.you.util.Constants.DEFAULT_DRAWER_ITEM
import com.example.you.util.Constants.UNDERLINED_DRAWER_ITEM

typealias onMenuClick = (drawerItem:DrawerItem?) -> Unit
typealias onLogOutClick = (position: Int) -> Unit

class DrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val itemList = mutableListOf<DrawerItem>()
    lateinit var onMenuClick: onMenuClick
    lateinit var onLogOutClick: onLogOutClick

    inner class DrawerViewHolder(val binding: RowDrawerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var model: DrawerItem
        fun onBind() {
            model = itemList[absoluteAdapterPosition]
            binding.tvItemName.text = model.itemTitle
            binding.ivIcon.setImageResource(model.icon)
            binding.root.setOnClickListener {
                onMenuClick.invoke(model)
                onLogOutClick.invoke(absoluteAdapterPosition)
            }
        }

    }

    inner class SecondDrawerViewHolder(val binding: RowDrawerItemUnderlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var model: DrawerItem
        fun onBind() {
            model = itemList[absoluteAdapterPosition]
            binding.tvItemName.text = model.itemTitle
            binding.ivIcon.setImageResource(model.icon)
            binding.root.setOnClickListener {
                onMenuClick.invoke(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DEFAULT_DRAWER_ITEM) {
            DrawerViewHolder(
                RowDrawerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            SecondDrawerViewHolder(
                RowDrawerItemUnderlineBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DrawerViewHolder -> holder.onBind()
            is SecondDrawerViewHolder -> holder.onBind()
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun setData(newList: MutableList<DrawerItem>) {
        this.itemList.apply {
            clear()
            addAll(newList)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val model = itemList[position]
        return if (model.type == DEFAULT_DRAWER_ITEM)
            DEFAULT_DRAWER_ITEM
        else
            UNDERLINED_DRAWER_ITEM
    }
}