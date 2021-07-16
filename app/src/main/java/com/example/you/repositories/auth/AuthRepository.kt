package com.example.you.repositories.auth


import android.net.Uri
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
        userName: String,
        lat: Number,
        long: Number,
        imageUri: Uri
    ): Resource<AuthResult>

    suspend fun logIn(email: String, password: String): Resource<AuthResult>
}