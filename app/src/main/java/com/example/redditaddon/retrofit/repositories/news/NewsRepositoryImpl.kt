package com.example.redditaddon.retrofit.repositories.news

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.redditaddon.activities.main.pager.PublicationsPagingSource
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.client.RetrofitClient
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
        val res = apiClient.client.uploadMorePublications(after)
        if (res.isSuccessful) {
            return res.body()?.toPublicationItemList() ?: listOf()
        }
        return listOf()    }

    override fun getPagedPublications(): Pager<String, PublicationItem> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PublicationsPagingSource(this) }
        )
    }
}