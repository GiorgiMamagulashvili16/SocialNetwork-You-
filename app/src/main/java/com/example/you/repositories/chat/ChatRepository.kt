package com.example.you.repositories.chat

import com.example.you.models.message.Message
import com.example.you.util.Resource

interface ChatRepository {
    suspend fun sendMessage(receiverId:String,text:String): Resource<Any>
    suspend fun readMessage(receiverId: String): Resource<List<Message>>
}