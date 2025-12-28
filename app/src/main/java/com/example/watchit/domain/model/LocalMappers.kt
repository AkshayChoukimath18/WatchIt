package com.example.watchit.domain.model

import com.example.watchit.data.local.LibraryEntity
import com.example.watchit.data.local.LibraryWithMovies
import com.example.watchit.data.local.MovieEntity

fun LibraryEntity.toDomain(): Library =
    Library(
        id = id,
        name = name,
        description = description,
        visibility = when(visibility) {
            "PRIVATE" -> LibraryVisibility.PRIVATE
            "PUBLIC" -> LibraryVisibility.PUBLIC
            "SHARED" -> LibraryVisibility.SHARED
            else -> LibraryVisibility.PRIVATE
        },
        createdAt = createdAt
    )

fun MovieEntity.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
        sourceUrl = sourceUrl,
        sourcePlatform = sourcePlatform,
        createdAt = createdAt
    )

fun LibraryWithMovies.toDomain(): LibraryWithMoviesDomain =
    LibraryWithMoviesDomain(
        library = library.toDomain(),
        movies = movies.map { it.toDomain() }
    )