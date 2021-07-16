package com.example.you.repositories.auth


import android.net.Uri
import com.example.you.models.user.UserModel
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(): AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val storage = Firebase.storage
    override suspend fun register(
        email: String,
        password: String,
        userName: String,
        lat: Number,
        long: Number,
        imageUri: Uri
    ): Resource<AuthResult> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid!!
            val uploadImage = storage.getReference(uid).putFile(imageUri).await()
            val imageUrl = uploadImage.metadata?.reference?.downloadUrl?.await().toString()
            val user = UserModel(
                uid,
                userName,
                lat = lat,
                long = long,
                profileImageUrl = imageUrl
            )
            users.document(uid).set(user).await()
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun logIn(email: String, password: String): Resource<AuthResult> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }


}