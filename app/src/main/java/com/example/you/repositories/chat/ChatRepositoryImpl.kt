package com.example.you.repositories.chat

import android.util.Log
import com.example.you.models.message.Message
import com.example.you.models.notification.NotificationData
import com.example.you.models.notification.PushNotification
import com.example.you.network.NotificationService
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Constants.CHAT_COLLECTION_NAME
import com.example.you.util.Constants.TIME_FIELD
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val postRepository: PostRepositoryImp,
    private val notificationService: NotificationService
) : ChatRepository {
    private val chatCollection = fireStore.collection(CHAT_COLLECTION_NAME)
    private var topic = ""

    override suspend fun sendMessage(receiverId: String, text: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val currentUser = auth.currentUser?.uid!!
                val userName =postRepository.getUser(currentUser).data!!.userName

                val message = Message(
                    messageId = UUID.randomUUID().toString(),
                    senderId = currentUser,
                    receiverId = receiverId,
                    message = text,
                    time = System.currentTimeMillis()
                )
                topic = "/topics/$receiverId"
                PushNotification(NotificationData(currentUser,userName, text), topic).also {
                    sendNotification(it)
                }
                chatCollection.document(message.messageId).set(message).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun readMessage(receiverId: String): Resource<List<Message>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val currentUserId = auth.currentUser?.uid!!
                val result = mutableListOf<Message>()
                chatCollection.orderBy(TIME_FIELD, Query.Direction.ASCENDING).get().await()
                    .toObjects(Message::class.java).onEach { message ->
                        if (message.senderId == currentUserId && message.receiverId == receiverId ||
                            message.senderId == receiverId && message.receiverId == currentUserId)
                            result.add(
                                message
                            )
                        val senderUser = postRepository.getUser(message.senderId).data!!
                        message.apply {
                            senderProfileImage = senderUser.profileImageUrl
                            senderUserName = senderUser.userName
                        }
                    }
                Resource.Success(result)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun sendNotification(notification: PushNotification): Unit = withContext(Dispatchers.IO){
        try {
            val response = notificationService.sendNotification(notification)
            if (response.isSuccessful) {
                val body = response.body()!!
                Log.d("NOTREPSONSE", "$body")
            } else {
                Log.d("NOTREPSONSE", "${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.d("NOTREPSONSEC", "$e")
        }
    }
}