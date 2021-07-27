package com.example.you.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.you.R
import com.example.you.util.Constants.CHANNEL_DESCRIPTION
import com.example.you.util.Constants.CHANNEL_ID
import com.example.you.util.Constants.CHANNEL_NAME
import com.example.you.util.Constants.NOTIFICATION_MESSAGE
import com.example.you.util.Constants.NOTIFICATION_TITLE
import com.example.you.util.Constants.TOKEN
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class PushNotificationService : FirebaseMessagingService() {

    companion object{
        var sharedPref:SharedPreferences? = null
        var token:String?
        get() {
            return sharedPref?.getString(TOKEN,"")
        }
        set(value){
            sharedPref?.edit()?.putString(TOKEN,value)?.apply()
        }
    }
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val args = Bundle()
        args.putString("receiverId",message.data["senderId"])
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.dashboard_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(args)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data[NOTIFICATION_TITLE])
            .setContentText(message.data[NOTIFICATION_MESSAGE])
            .setSmallIcon(R.drawable.ic_mes)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName =CHANNEL_NAME
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CHANNEL_DESCRIPTION
            enableLights(true)
            lightColor = Color.WHITE
        }
        notificationManager.createNotificationChannel(channel)
    }
}