package com.example.watchit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val year: Int? = null,
    val posterUrl: String? = null,
    val sourceUrl: String,
    val sourcePlatform: String,
    val createdAt: Long = System.currentTimeMillis()

)
