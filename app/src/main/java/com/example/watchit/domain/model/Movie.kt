package com.example.watchit.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val posterUrl: String?,
    val sourceUrl: String,
    val sourcePlatform: String,
    val createdAt: Long
)
