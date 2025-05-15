package com.example.redditaddon.retrofit.repositories.news

import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.client.RetrofitClient
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiClient : RetrofitClient
) : NewsRepository {
    override suspend fun getPubs(): List<PublicationItem> {
        apiClient.client.getTopPublications()
        return listOf()
    }

    override suspend fun updatePubs(): List<PublicationItem> {
        return listOf()
    }
}