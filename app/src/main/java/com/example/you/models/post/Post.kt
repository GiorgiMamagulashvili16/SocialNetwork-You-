package com.example.you.models.post

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val postImageUrl: String = "",
    @get:Exclude var authorProfileImageUrl: String = "",
    @get:Exclude var authorUserName: String = "",
    @get:Exclude var isLiked: Boolean = false,
    var likedBy: List<String> = listOf(),
    val date: Long = 0L,
)
