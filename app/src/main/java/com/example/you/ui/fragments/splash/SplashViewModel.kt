package com.example.you.ui.fragments.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(): ViewModel() {
    val auth = FirebaseAuth.getInstance()
    fun checkSession(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }
}