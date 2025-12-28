package com.example.watchit.ui.libraryDetails

import com.example.watchit.domain.model.Movie

data class LibraryDetailsUiState(
    val isLoading: Boolean = false,
    val isAdding: Boolean = false,
    val name: String = "",
    val movies: List<Movie> = emptyList(),
    val error: String? = null
)
