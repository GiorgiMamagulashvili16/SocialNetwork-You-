package com.example.you.repositories.posts

import android.net.Uri
import android.util.Log
import com.example.you.models.post.Comment
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants.COMMENTS_COLLECTION_NAME
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.USER_COLLECTION_NAME
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun addPost(imageUri: Uri, postText: String, postType: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid!!
                val postId = UUID.randomUUID().toString()
                val postImageUpl = storage.getReference(postId).putFile(imageUri).await()
                val postImage = postImageUpl.metadata?.reference?.downloadUrl?.await().toString()
                val post = Post(
                    postId = postId,
                    authorId = uid,
                    text = postText,
                    postImageUrl = postImage,
                    postType = postType,
                    date = System.currentTimeMillis()
                )
                postCollection.document(postId).set(post).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getUser(uid: String): Resource<UserModel> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = userCollection.document(uid).get().await()
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
                val allComments = commentCollection.whereEqualTo("postId", postId).get().await()
                    .toObjects(Comment::class.java)
                Resource.Success(allComments)
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

            Log.d("HEUJEU", "${isLiked}")
            Resource.Success(isLiked)
        } catch (e: Exception) {
            Log.d("HEUJEUEE", "${e}")
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
                        i["uid"] as String,
                        i["userName"] as String,
                        i["profileImageUrl"] as String,
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
                val user = userCollection.whereIn("uid", uids).get().await()
                user.documents.forEach {
                    val userr = UserModel(
                        uid = it["uid"] as String,
                        userName = it["userName"] as String,
                        profileImageUrl = it["profileImageUrl"] as String
                    )
                    userList.add(userr)
                }
                Resource.Success(userList)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }

        }


}