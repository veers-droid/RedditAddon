package com.example.redditaddon.activities.main.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.repositories.news.NewsRepository

class PublicationsPagingSource(
    private val repository: NewsRepository
) : PagingSource<String, PublicationItem>() {
    override fun getRefreshKey(state: PagingState<String, PublicationItem>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PublicationItem> {
        return try {
            val after = params.key.orEmpty()
            val pubs = repository.updatePubs(after)
            val nextKey = if (pubs.isNotEmpty()) pubs.last().id else null
            LoadResult.Page(
                data = pubs,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}