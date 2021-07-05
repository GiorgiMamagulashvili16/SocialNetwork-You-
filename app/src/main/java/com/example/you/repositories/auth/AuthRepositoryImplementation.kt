package com.example.you.repositories.auth

import com.example.you.models.user.User
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult

class AuthRepositoryImplementation:AuthRepository {

    override suspend fun register(user: User): Resource<AuthResult> {
        TODO("Not yet implemented")
    }
}