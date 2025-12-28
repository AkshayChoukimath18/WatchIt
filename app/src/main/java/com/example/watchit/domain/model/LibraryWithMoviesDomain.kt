package com.example.watchit.domain.model

data class LibraryWithMoviesDomain(
    val library: Library,
    val movies: List<Movie>
)
