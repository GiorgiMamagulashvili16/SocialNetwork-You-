package com.example.you.models.message

import com.google.firebase.firestore.Exclude

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    @get:Exclude var senderProfileImage: String = "",
    @get:Exclude var senderUserName: String = "",
    val message: String = "",
    val time: Long = 0L
)
