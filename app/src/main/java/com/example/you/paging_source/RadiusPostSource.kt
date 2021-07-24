package com.example.you.paging_source

import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants
import com.example.you.util.Constants.AUTHOR_ID_FIELD
import com.example.you.util.Constants.CURRENT_USER_LOCATION
import com.example.you.util.Constants.DISTANCE_RADIUS
import com.example.you.util.Constants.OTHER_USER_LOCATION
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class RadiusPostSource(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val location: Location
) : PagingSource<QuerySnapshot, Post>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val uid = auth.currentUser?.uid!!
            val currentPage = params.key ?: fireStore.collection(POSTS_COLLECTION_NAME)
                .whereNotEqualTo(AUTHOR_ID_FIELD, uid)
                .get()
                .await()
            val lastDocument = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStore.collection(POSTS_COLLECTION_NAME)
                .whereNotEqualTo(AUTHOR_ID_FIELD, auth.currentUser?.uid!!)
                .startAfter(lastDocument)
                .get()
                .await()
            val result = mutableListOf<Post>()
            currentPage.toObjects(Post::class.java).onEach { post ->
                val currentUser =
                    fireStore.collection(Constants.USER_COLLECTION_NAME).document(post.authorId)
                        .get()
                        .await()
                val currentUserModel = UserModel(
                    currentUser["uid"] as String,
                    currentUser["userName"] as String,
                    currentUser["description"] as String,
                    currentUser["lat"] as Double,
                    currentUser["long"] as Double,
                    profileImageUrl = currentUser["profileImageUrl"] as String,
                )
                post.apply {
                    authorUserName = currentUserModel.userName
                    authorProfileImageUrl = currentUserModel.profileImageUrl
                    isLiked = auth.currentUser?.uid!! in post.likedBy
                }
                val currentUserLocation = Location(CURRENT_USER_LOCATION).apply {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                val otherUserLocation = Location(OTHER_USER_LOCATION).apply {
                    latitude = currentUserModel.lat as Double
                    longitude = currentUserModel.long as Double
                }
                if (currentUserLocation.distanceTo(otherUserLocation) < DISTANCE_RADIUS) {
                    result.add(post)
                }
            }
            LoadResult.Page(
                result,
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}