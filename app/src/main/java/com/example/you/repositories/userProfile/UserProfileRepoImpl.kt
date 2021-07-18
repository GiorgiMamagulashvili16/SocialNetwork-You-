package com.example.you.repositories.userProfile

import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.USER_COLLECTION_NAME
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
): UserProfileRepository {
    private val users = fireStore.collection(USER_COLLECTION_NAME)
    private val posts = fireStore.collection(POSTS_COLLECTION_NAME)

    override suspend fun getUser(uid: String): Resource<UserModel> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = users.document(uid).get().await()
            val currentUserModel = UserModel(
                currentUser["uid"] as String,
                currentUser["userName"] as String,
                currentUser["description"] as String,
                currentUser["lat"] as Double,
                currentUser["long"] as Double,
                currentUser["profileImageUrl"] as String,
            )
            Resource.Success(currentUserModel)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
    override suspend fun getUserProfilePosts(uid: String): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val userPosts =
                    posts.whereEqualTo("authorId", uid).get().await().toObjects(Post::class.java)
                Resource.Success(userPosts)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }


}