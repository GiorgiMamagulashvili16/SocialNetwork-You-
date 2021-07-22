package com.example.you.models.post

import com.google.firebase.firestore.IgnoreExtraProperties

data class Comment(
    val commentId: String = "",
    val authorId: String = "",
    val postId: String = "",
    val authorUsername: String = "",
    val authorProfileImage: String = "",
    val commentText: String = "",
    val date: Long = 0L,
)
