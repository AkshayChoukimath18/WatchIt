package com.example.watchit.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LibraryWithMovies(

    @Embedded
    val library: LibraryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LibraryMovieCrossRef::class,
            parentColumn = "libraryId",
            entityColumn = "movieId"
        )
    )
    val movies: List<MovieEntity>

)
