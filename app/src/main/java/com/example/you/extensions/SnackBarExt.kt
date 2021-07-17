package com.example.you.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.createInfoSnackBar(message: String, color: Int) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).apply {
        setTextColor(color)
    }.show()
}