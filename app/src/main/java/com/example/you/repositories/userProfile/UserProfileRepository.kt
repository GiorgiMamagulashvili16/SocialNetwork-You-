package com.example.you.repositories.userProfile

import android.net.Uri
import com.example.you.models.post.Post
import com.example.you.models.user.ProfileUpdate
import com.example.you.models.user.UserModel
import com.example.you.util.Resource

interface UserProfileRepository {

    suspend fun getUserProfilePosts(uid: String): Resource<List<Post>>
    suspend fun getUser(uid: String): Resource<UserModel>
    suspend fun updateProfileImage(uid: String, imageUri: Uri):Resource<Uri>
    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>
    suspend fun getUserPosts(authorId:String):Resource<List<Post>>
    suspend fun deleteCommentByPostId(postId: String):Resource<Any>
}