package com.example.redditaddon.retrofit.repositories.news

import android.util.Log
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiClient : RetrofitClient
) : NewsRepository {
    override suspend fun getPubs(): List<PublicationItem> {
        val res = apiClient.client.getTopPublications()
        Log.d("request", res.toString())
        if (res.isSuccessful) {
            return res.body()?.toPublicationItemList() ?: listOf()
        }
        return listOf()
    }

    override suspend fun updatePubs(after: String): List<PublicationItem> {
        return listOf()
    }
}