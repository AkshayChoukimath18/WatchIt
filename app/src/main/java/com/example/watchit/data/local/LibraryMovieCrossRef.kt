package com.example.watchit.data.local

import androidx.room.Entity


@Entity(tableName = "library_movies",
    primaryKeys = ["libraryId", "movieId"]
)
data class LibraryMovieCrossRef(
    val libraryId: Long,
    val movieId: Long
)
