package com.example.you.paging_source

import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants
import com.example.you.util.Constants.CURRENT_USER_LOCATION
import com.example.you.util.Constants.DATE_FIELD
import com.example.you.util.Constants.DISTANCE_RADIUS
import com.example.you.util.Constants.OTHER_POST_LOCATION
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class RadiusPostSource(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val location: Location
) : PagingSource<QuerySnapshot, Post>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
       return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val uid = auth.currentUser?.uid!!
            val currentPage = params.key ?: fireStore.collection(POSTS_COLLECTION_NAME)
                .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
                .get()
                .await()
            val lastDocument = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStore.collection(POSTS_COLLECTION_NAME)
                .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
                .startAfter(lastDocument)
                .get()
                .await()
            val result = mutableListOf<Post>()
            currentPage.toObjects<Post>().onEach { post ->
                val currentUser =
                    fireStore.collection(Constants.USER_COLLECTION_NAME).document(post.authorId)
                        .get()
                        .await()
                        .toObject<UserModel>()!!

                post.apply {
                    authorUserName = currentUser.userName
                    authorProfileImageUrl = currentUser.profileImageUrl
                    isLiked = auth.currentUser?.uid!! in post.likedBy
                }
                val currentUserLocation = Location(CURRENT_USER_LOCATION).apply {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                val postLocation = Location(OTHER_POST_LOCATION).apply {
                    latitude = post.lat
                    longitude = post.long
                }
                if (currentUserLocation.distanceTo(postLocation) < DISTANCE_RADIUS && post.authorId != uid){
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