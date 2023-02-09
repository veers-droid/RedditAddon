package com.example.redditaddon.retrofit.services

import com.example.redditaddon.model.Publication
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("top.json?t=day&limit=40")
    fun getTopPublications(): Call<Publication>

    @GET("top.json?t=day")
    fun uploadMorePublications(@Query("after") name:String): Call<Publication>
}