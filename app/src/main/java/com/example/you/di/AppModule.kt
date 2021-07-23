package com.example.you.di

import com.example.you.repositories.auth.AuthRepository
import com.example.you.repositories.auth.AuthRepositoryImpl
import com.example.you.repositories.posts.PostRepository
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.repositories.userProfile.UserProfileRepoImpl
import com.example.you.repositories.userProfile.UserProfileRepository
import com.example.you.util.ResponseHandler
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
        fireStore: FirebaseFirestore,
        responseHandler: ResponseHandler
    ): PostRepository = PostRepositoryImp(auth, storage, fireStore,responseHandler)

    @Provides
    @Singleton
    fun provideProfileRepo(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fireStore: FirebaseFirestore
    ): UserProfileRepository = UserProfileRepoImpl(auth, storage, fireStore)

    @Provides
    @Singleton
    fun providesResponseHandler() = ResponseHandler()
}