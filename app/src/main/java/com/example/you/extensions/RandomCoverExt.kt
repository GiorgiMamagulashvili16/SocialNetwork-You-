package com.example.you.extensions

import androidx.appcompat.widget.AppCompatImageView
import com.example.you.R

fun AppCompatImageView.setRandomCover() {
    val covers = mutableListOf(R.mipmap.cover_1, R.mipmap.cover_2, R.mipmap.cover_3)
    this.setImageResource(covers.random())
}