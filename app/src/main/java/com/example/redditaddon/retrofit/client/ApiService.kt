package com.example.redditaddon.retrofit.client

import com.example.redditaddon.model.Publication
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top.json?t=day&limit=40")
    suspend fun getTopPublications(): Response<Publication>

    @GET("top.json?t=day")
    suspend fun uploadMorePublications(@Query("after") name:String): Response<Publication>
}