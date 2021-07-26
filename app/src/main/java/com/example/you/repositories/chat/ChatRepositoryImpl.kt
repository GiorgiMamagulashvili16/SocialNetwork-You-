package com.example.you.repositories.chat

import com.example.you.models.message.Message
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
    private val postRepository: PostRepositoryImp
) : ChatRepository {
    private val chatCollection = fireStore.collection(CHAT_COLLECTION_NAME)
    override suspend fun sendMessage(receiverId: String, text: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val message = Message(
                    messageId = UUID.randomUUID().toString(),
                    senderId = auth.currentUser?.uid!!,
                    receiverId = receiverId,
                    message = text,
                    time = System.currentTimeMillis()
                )
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
}