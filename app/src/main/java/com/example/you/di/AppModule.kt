package com.example.you.di

import com.example.you.network.NotificationService
import com.example.you.network.news.CovidInfoApi
import com.example.you.network.news.WeatherApi
import com.example.you.repositories.auth.AuthRepository
import com.example.you.repositories.auth.AuthRepositoryImpl
import com.example.you.repositories.chat.ChatRepository
import com.example.you.repositories.chat.ChatRepositoryImpl
import com.example.you.repositories.news.NewsRepository
import com.example.you.repositories.news.NewsRepositoryImp
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val NOTIFICATION_BASE_URL = "https://fcm.googleapis.com"
    private const val COVID_BASE_URL = "https://corona.lmao.ninja"
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org"

    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fireStore: FirebaseFirestore,
        handler: ResponseHandler
    ): AuthRepository = AuthRepositoryImpl(auth, storage, fireStore,handler)

    @Provides
    @Singleton
    fun providePostRepo(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fireStore: FirebaseFirestore,
    ): PostRepository = PostRepositoryImp(auth, storage, fireStore)

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

    @Provides
    @Singleton
    fun providesNotificationApi(): NotificationService = Retrofit.Builder()
        .baseUrl(NOTIFICATION_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideChatRepo(
        fireStore: FirebaseFirestore,
        auth: FirebaseAuth,
        postRepositoryImp: PostRepositoryImp,
        notificationService: NotificationService
    ): ChatRepository =
        ChatRepositoryImpl(fireStore, auth, postRepositoryImp, notificationService)

    @Provides
    @Singleton
    fun provideCovidApi(): CovidInfoApi = Retrofit.Builder()
        .baseUrl(COVID_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CovidInfoApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideNewsRepository(covidInfoApi: CovidInfoApi, weatherApi: WeatherApi): NewsRepository =
        NewsRepositoryImp(covidInfoApi, weatherApi)
}