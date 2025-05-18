package com.example.redditaddon.retrofit.repositories.news

import androidx.paging.Pager
import com.example.redditaddon.model.PublicationItem

interface NewsRepository {
    suspend fun getPubs() : List<PublicationItem>

    suspend fun updatePubs(after : String) : List<PublicationItem>

    fun getPagedPublications() : Pager<String, PublicationItem>
}