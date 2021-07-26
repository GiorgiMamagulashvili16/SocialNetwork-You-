package com.example.you.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.you.service.PushNotificationService
import com.example.you.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPushNotification()
    }

    private fun initPushNotification() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        PushNotificationService.sharedPref = getSharedPreferences("user_preference",Context.MODE_PRIVATE)
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
            PushNotificationService.token = it.result.token
        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$uid")
    }
}