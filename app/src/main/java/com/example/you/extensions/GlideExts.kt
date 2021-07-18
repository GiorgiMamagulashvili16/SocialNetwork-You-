package com.example.you.extensions

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.you.R
import com.google.android.material.imageview.ShapeableImageView

fun ShapeableImageView.getShapeableImage(url: String) {
    Glide.with(context).load(url).placeholder(R.mipmap.user_default_image).into(this)
}
fun AppCompatImageView.getImageFromUrl(url: String){
    Glide.with(context).load(url).into(this)
}