package com.example.you.repositories.userProfile

import android.net.Uri
import com.example.you.models.post.Post
import com.example.you.models.user.ProfileUpdate
import com.example.you.models.user.UserModel
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
) : UserProfileRepository {
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

    override suspend fun updateProfileImage(uid: String, imageUri: Uri): Resource<Uri> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val user = getUser(uid).data!!
                storage.getReferenceFromUrl(user.profileImageUrl).delete().await()

                val uriUpload = storage.getReference(uid).putFile(imageUri)
                    .await()
                val newUri = uriUpload.metadata?.reference?.downloadUrl?.await()
                Resource.Success(newUri!!)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }


    override suspend fun updateProfile(
        profileUpdate: ProfileUpdate
    ): Resource<Any> = withContext(Dispatchers.IO) {
        return@withContext try {

            val imageUrl = profileUpdate.profileImageUri?.let {
                updateProfileImage(profileUpdate.uid, it).data.toString()
            }
            val infoMap = mutableMapOf(
                "userName" to profileUpdate.userName,
                "description" to profileUpdate.description
            )
            imageUrl?.let {
                infoMap["profileImageUrl"] = it
            }
            users.document(profileUpdate.uid).update(infoMap.toMap()).await()

            Resource.Success(Any())
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

    override suspend fun deletePost(postId: String): Resource<Any> = withContext(Dispatchers.IO) {
        return@withContext try {
            posts.document(postId).delete().await()
            Resource.Success(Any())
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
    override suspend fun getUserPosts(authorId: String): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val userPosts = posts.whereEqualTo("authorId", authorId).get().await()
                    .toObjects(Post::class.java)
                Resource.Success(userPosts)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }
}