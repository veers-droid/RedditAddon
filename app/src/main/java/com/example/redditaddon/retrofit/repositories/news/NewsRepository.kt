package com.example.redditaddon.retrofit.repositories.news

import com.example.redditaddon.model.PublicationItem

interface NewsRepository {
    suspend fun getPubs() : List<PublicationItem>

    suspend fun updatePubs() : List<PublicationItem>
}