package com.example.you.paging_source

import android.util.Log.d
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants
import com.example.you.util.Constants.DATE_FIELD
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.POST_TYPE_FOR_RADIUS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class AllPostSource(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PagingSource<QuerySnapshot, Post>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val uid = auth.currentUser?.uid!!
            val result = mutableListOf<Post>()

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

            currentPage.toObjects<Post>().onEach { post ->
                val currentUser =
                    fireStore.collection(Constants.USER_COLLECTION_NAME).document(post.authorId)
                        .get()
                        .await().toObject<UserModel>()!!
                post.apply {
                    authorUserName = currentUser.userName
                    authorProfileImageUrl = currentUser.profileImageUrl
                    isLiked = uid in post.likedBy
                }
                if (post.authorId != uid && post.postType != POST_TYPE_FOR_RADIUS)
                    result.add(post)
            }


            LoadResult.Page(
                result,
                null,
                nextPage
            )
        } catch (e: Exception) {
            d("POSTERROR", "$e")
            LoadResult.Error(e)

        }
    }
}