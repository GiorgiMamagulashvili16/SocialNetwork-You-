package com.example.you.ui.fragments.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    fun checkSession(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }
}