package com.example.watchit.ui.homepage

import com.example.watchit.domain.model.Movie


data class HomeUiState(
    val sections: List<LibrarySection> = emptyList()
)

data class LibrarySection(
    val libraryTitle: String,
    val movies: List<Movie>
)
