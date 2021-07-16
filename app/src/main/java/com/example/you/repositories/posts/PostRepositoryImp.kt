package com.example.you.repositories.posts

import android.net.Uri
import android.util.Log.d
import com.example.you.models.post.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*

class PostRepositoryImp : PostRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val posts = firestore.collection("posts")
    private val storage = Firebase.storage

    override suspend fun addPost(imageUrl: Uri, postText: String) {
        try {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUpload = storage.getReference(postId).putFile(imageUrl).await()
            val postImageUrl = imageUpload?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(postId, uid, postText, postImageUrl, data = System.currentTimeMillis())
            posts.document(postId).set(post).await()
        } catch (e: Exception) {
            d("AddPostError", "$e")
        }
    }

}