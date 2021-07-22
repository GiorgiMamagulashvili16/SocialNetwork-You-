package com.example.you.repositories.posts

import android.location.Location
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
import com.google.firebase.firestore.Query
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
    private val users = fireStore.collection(USER_COLLECTION_NAME)
    private val posts = fireStore.collection(POSTS_COLLECTION_NAME)
    private val comments = fireStore.collection(COMMENTS_COLLECTION_NAME)

    override suspend fun addPost(imageUri: Uri, postText: String): Resource<Any> =
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
                    date = System.currentTimeMillis()
                )
                posts.document(postId).set(post).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getAllPost(): Resource<List<Post>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val uid = auth.uid!!
            val allPosts =
                posts.whereNotEqualTo("authorId", uid)
                    .orderBy("authorId", Query.Direction.DESCENDING)
                    .get()
                    .await().toObjects(Post::class.java).onEach {
                        val user = getUser(it.authorId).data!!
                        it.apply {
                            authorUserName = user.userName
                            authorProfileImageUrl = user.profileImageUrl
                            isLiked = uid in it.likedBy
                        }
                    }
            Resource.Success(allPosts)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun getNearbyPosts(location: Location): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val uid = auth.uid!!
                val nearbyPost = mutableListOf<Post>()

                posts.whereNotEqualTo("authorId", uid).get().await().toObjects(Post::class.java)
                    .forEach {
                        val user = getUser(it.authorId).data!!
                        it.apply {
                            authorUserName = user.userName
                            authorProfileImageUrl = user.profileImageUrl
                            isLiked = uid in it.likedBy
                        }
                        val currentUserLocation = Location("CurrentUserLocation").apply {
                            latitude = location.latitude
                            longitude = location.longitude
                        }
                        val userLocation = Location("userLocation").apply {
                            latitude = user.lat as Double
                            longitude = user.long as Double
                        }
                        if (currentUserLocation.distanceTo(userLocation) < 5000.0)
                            nearbyPost.add(it)
                    }
                Resource.Success(nearbyPost)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

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
                comments.document(commentId).set(comment).await()
                Resource.Success(Any())
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun getCommentForPost(postId: String): Resource<List<Comment>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val allComments = comments.whereEqualTo("postId", postId).get().await()
                    .toObjects(Comment::class.java)
                Resource.Success(allComments)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }

    override suspend fun deleteComment(commentId: String): Resource<Any> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                comments.document(commentId).delete().await()
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
                val postResult = transaction.get(posts.document(post.postId))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    posts.document(post.postId),
                    "likedBy",
                    if(uid in currentLikes) currentLikes - uid else {
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

}