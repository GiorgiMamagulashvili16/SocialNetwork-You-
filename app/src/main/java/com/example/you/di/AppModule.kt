package com.example.you.di

import com.example.you.repositories.auth.AuthRepository
import com.example.you.repositories.auth.AuthRepositoryImpl
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
    fun provideAuthRepo() = AuthRepositoryImpl() as AuthRepository
}