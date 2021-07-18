package com.example.you.repositories.userProfile

import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Resource

interface UserProfileRepository {

    suspend fun getUserProfilePosts(uid: String): Resource<List<Post>>
    suspend fun getUser(uid: String): Resource<UserModel>
}