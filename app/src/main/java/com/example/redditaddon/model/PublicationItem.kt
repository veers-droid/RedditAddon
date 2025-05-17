package com.example.redditaddon.model

data class PublicationItem(
    val id : String,
    val thumbNail : ThumbNail,
    val commentsCount: Int,
    val author : String,
    val created : Double,
    val text : String,
)

data class  ThumbNail(
    val image : String,
    val thumbNailHeight: Double,
    val thumbNailWidth: Double
)
