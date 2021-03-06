package com.example.you.repositories.posts

import android.net.Uri
import com.example.you.models.post.Comment
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants.COMMENTS_COLLECTION_NAME
import com.example.you.util.Constants.DATE_FIELD
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.USER_COLLECTION_NAME
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PostRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
) : PostRepository {
    private val userCollection = fireStore.collection(USER_COLLECTION_NAME)
    private val postCollection = fireStore.collection(POSTS_COLLECTION_NAME)
    private val commentCollection = fireStore.collection(COMMENTS_COLLECTION_NAME)

    override suspend fun addPost(
        imageUri: Uri,
        postText: String,
        postType:
        String, lat: Double,
        long: Double
    ): Resource<Any> =
        withContext(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid!!
                val postId = UUID.randomUUID().toString()
                val postImageUpl = storage.getReference(postId).putFile(imageUri).await()
                val postImage = postImageUpl.metadata?.reference?.downloadUrl?.await().toString()
                val post = mutableMapOf(
                    "authorId" to uid,
                    "date" to System.currentTimeMillis(),
                    "lat" to lat as Number,
                    "long" to long as Number,
                    "postId" to postId,
                    "postImageUrl" to postImage,
                    "postType" to postType,
                    "text" to postText
                )
                postCollection.document(postId).set(post).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getUser(uid: String): Resource<UserModel> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = userCollection.document(uid).get().await().toObject<UserModel>()
            Resource.Success(currentUser!!)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun addComment(postId: String, text: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val commentId = UUID.randomUUID().toString()
                val authorId = auth.currentUser?.uid!!
                val user = getUser(authorId).data!!
                val comment =
                    Comment(
                        commentId = commentId,
                        authorId = authorId,
                        postId = postId,
                        authorUsername = user.userName,
                        authorProfileImage = user.profileImageUrl,
                        date = System.currentTimeMillis(),
                        commentText = text
                    )
                commentCollection.document(commentId).set(comment).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getCommentForPost(postId: String): Resource<List<Comment>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = mutableListOf<Comment>()
                commentCollection.orderBy(DATE_FIELD, Query.Direction.DESCENDING).get().await()
                    .toObjects<Comment>().forEach { comment ->
                        if (comment.postId == postId) result.add(comment)

                    }
                Resource.Success(result)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun deleteComment(commentId: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                commentCollection.document(commentId).delete().await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getPostLikes(post: Post): Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            var isLiked = false
            fireStore.runTransaction { transaction ->
                val uid = auth.uid!!
                val postResult = transaction.get(postCollection.document(post.postId))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    postCollection.document(post.postId),
                    "likedBy",
                    if (uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            Resource.Success(isLiked)

        } catch (e: Exception) {
            Resource.Error(e.toString())
        }


    }

    override suspend fun searchUser(query: String): Resource<List<UserModel>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val userList = mutableListOf<UserModel>()
                val users = userCollection.whereGreaterThanOrEqualTo(
                    "userName", query.toUpperCase(
                        Locale.ROOT
                    )
                ).get().await()
                for (i in users.documents) {
                    val user = UserModel(
                        uid = i["uid"] as String,
                        userName = i["userName"] as String,
                        profileImageUrl = i["profileImageUrl"] as String,
                    )
                    userList.add(user)
                }
                Resource.Success(userList)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getLikedByUsers(uids: List<String>): Resource<List<UserModel>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val userList = mutableListOf<UserModel>()
                val users = userCollection.whereIn("uid", uids).get().await()
                users.documents.forEach {
                    val user = UserModel(
                        uid = it["uid"] as String,
                        userName = it["userName"] as String,
                        profileImageUrl = it["profileImageUrl"] as String
                    )
                    userList.add(user)
                }
                Resource.Success(userList)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }

        }

    override suspend fun deletePost(postId: String): Resource<Any> = withContext(Dispatchers.IO) {
        return@withContext try {
            storage.getReference(postId).delete().await()
            postCollection.document(postId).delete().await()
            Resource.Success(Any())
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
}