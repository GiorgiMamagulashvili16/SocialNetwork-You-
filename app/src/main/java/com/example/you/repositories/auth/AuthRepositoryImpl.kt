package com.example.you.repositories.auth


import android.net.Uri
import com.example.you.models.user.UserModel
import com.example.you.util.Constants.USER_COLLECTION_NAME
import com.example.you.util.Resource
import com.example.you.util.ResponseHandler
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    private val handler:ResponseHandler
) : AuthRepository {
    private val users = fireStore.collection(USER_COLLECTION_NAME)
    override suspend fun register(
        email: String,
        password: String,
        userName: String,
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
                profileImageUrl = imageUrl,
            )
            users.document(uid).set(user).await()
            handler.handleSuccess(result)
        } catch (e: Exception) {
            handler.handleException(e)
        }
    }

    override suspend fun logIn(email: String, password: String): Resource<AuthResult> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
               handler.handleSuccess(result)
            } catch (e: Exception) {
              handler.handleException(e)
            }
        }


}