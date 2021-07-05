package com.example.you.repositories.auth

import com.example.you.models.user.User
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun register(user:User):Resource<AuthResult>
}