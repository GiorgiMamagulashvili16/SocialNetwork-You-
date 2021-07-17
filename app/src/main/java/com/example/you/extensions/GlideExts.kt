package com.example.you.extensions

import com.bumptech.glide.Glide
import com.example.you.R
import com.google.android.material.imageview.ShapeableImageView

fun ShapeableImageView.getCircleImage(url: String) {
    Glide.with(context).load(url).placeholder(R.mipmap.user_default_image).into(this)
}