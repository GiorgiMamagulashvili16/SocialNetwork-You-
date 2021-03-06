package com.example.you.di

import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.POST_PAGE_SIZE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideFireStore() = FirebaseFirestore.getInstance()

}