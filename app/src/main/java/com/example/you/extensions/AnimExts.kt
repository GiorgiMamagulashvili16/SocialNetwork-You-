package com.example.you.extensions

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.you.R

fun View.animSlideUp(context: Context, time: Long, startOffSetTime: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = time
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffSetTime
    }
    startAnimation(slideUp)
}

fun slideUp(context: Context, vararg views: View, time: Long = 300L, delay: Long = 100L) {
    for (i in views.indices) {
        views[i].animSlideUp(context, time, i * delay)
    }
}