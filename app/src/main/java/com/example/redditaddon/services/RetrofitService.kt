package com.example.redditaddon.services

import com.example.redditaddon.model.Publication
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {
    @GET("top.json?t=day")
    fun getTopPublications(): Call<Publication>
}