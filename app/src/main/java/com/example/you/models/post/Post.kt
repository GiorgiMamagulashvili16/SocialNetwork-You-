package com.example.you.models.post

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val postImageUrl: String = "",
    @Exclude val authorProfileImageUrl: String = "",
    @Exclude val authorUserName: String = "",
    @Exclude val isLiked: Boolean = false,
    val likedBy: List<String> = listOf(),
    val data: Long = 0L,

    )
