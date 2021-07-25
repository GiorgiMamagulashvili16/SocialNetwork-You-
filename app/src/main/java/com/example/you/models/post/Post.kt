package com.example.you.models.post

import com.example.you.util.Constants.POST_TYPE_FOR_ALL
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val postImageUrl: String = "",
    val postType:String =POST_TYPE_FOR_ALL,
    @get:Exclude var authorProfileImageUrl: String = "",
    @get:Exclude var authorUserName: String = "",
    @get:Exclude var isLiked: Boolean = false,
    var likedBy: List<String> = listOf(),
    val date: Long = 0L,
    @get:Exclude var likeLoading: Boolean = false
)
