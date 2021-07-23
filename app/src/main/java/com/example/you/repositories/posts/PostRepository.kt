package com.example.you.repositories.posts

import android.location.Location
import android.net.Uri
import com.example.you.models.post.Comment
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Resource

interface PostRepository {

    suspend fun addPost(imageUri: Uri, postText: String): Resource<Any>
    suspend fun getAllPost(): Resource<List<Post>>
    suspend fun getNearbyPosts(location: Location): Resource<List<Post>>
    suspend fun getUser(uid: String): Resource<UserModel>
    suspend fun addComment(postId: String, text: String): Resource<Any>
    suspend fun getCommentForPost(postId: String): Resource<List<Comment>>
    suspend fun deleteComment(commentId: String): Resource<Any>
    suspend fun getPostLikes(post: Post): Resource<Boolean>
    suspend fun searchUser(query: String): Resource<List<UserModel>>
    suspend fun getLikedByUsers(uids: List<String>): Resource<List<UserModel>>
}