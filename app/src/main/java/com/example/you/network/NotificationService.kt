package com.example.you.network

import com.example.you.models.notification.PushNotification
import com.example.you.util.Constants
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    @Headers("Authorization: key=${Constants.SERVER_KEY}", "Content-type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}