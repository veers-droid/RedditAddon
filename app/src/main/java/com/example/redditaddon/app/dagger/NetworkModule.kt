package com.example.redditaddon.app.dagger

import com.example.redditaddon.retrofit.client.RetrofitClient
import com.example.redditaddon.retrofit.repositories.news.NewsRepository
import com.example.redditaddon.retrofit.repositories.news.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideApiClient() : RetrofitClient = RetrofitClient

    @Provides
    fun provideNewsRepository(apiClient : RetrofitClient) : NewsRepository = NewsRepositoryImpl(apiClient)

}