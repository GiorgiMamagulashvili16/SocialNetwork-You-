package com.example.you.di

import com.example.you.repositories.auth.AuthRepository
import com.example.you.repositories.auth.AuthRepositoryImpl
import com.example.you.repositories.posts.PostRepository
import com.example.you.repositories.posts.PostRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fireStore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(auth, storage, fireStore)

    @Provides
    @Singleton
    fun providePostRepo(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fireStore: FirebaseFirestore
    ): PostRepository = PostRepositoryImp(auth, storage, fireStore)
}