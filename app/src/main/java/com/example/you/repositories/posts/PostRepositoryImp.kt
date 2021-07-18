package com.example.you.repositories.posts

import android.location.Location
import android.net.Uri
import com.example.you.models.post.Post
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
import java.util.*
import javax.inject.Inject

class PostRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
) : PostRepository {
    private val users = fireStore.collection(USER_COLLECTION_NAME)
    private val posts = fireStore.collection(POSTS_COLLECTION_NAME)

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



}