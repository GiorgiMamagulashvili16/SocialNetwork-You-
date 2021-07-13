package com.example.you.repositories.auth


import com.example.you.models.user.UserModel
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImplement: AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        password: String,
        userName: String,
        status: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = auth.currentUser?.uid!!
                val user = UserModel(uid, userName, status)
                users.document(uid).set(user).await()
                Resource.Success(result)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }
    }

    override suspend fun getPosts(): Resource<List<UserModel>> {
        return try {
            val querySnapshot = users.get().await()
            val userList = mutableListOf<UserModel>()
            for (document in querySnapshot.documents) {
                val user = UserModel(
                    document.get("uid") as String,
                    document.get("userName") as String,
                    document.get("status") as String,
                )
                userList.add(user)
            }
            Resource.Success(userList)
        } catch (e: Exception) {
            Resource.Error(e.toString() ?: "unknown error")
        }
    }
}