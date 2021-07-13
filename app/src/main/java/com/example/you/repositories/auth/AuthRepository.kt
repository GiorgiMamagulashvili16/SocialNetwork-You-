package com.example.you.repositories.auth


import com.example.you.models.user.UserModel
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
        userName:String,
        status:String
    ): Resource<AuthResult>
    suspend fun getPosts():Resource<List<UserModel>>
}