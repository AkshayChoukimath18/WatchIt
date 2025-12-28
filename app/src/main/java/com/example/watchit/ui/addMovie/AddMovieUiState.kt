package com.example.watchit.ui.addMovie

import com.example.watchit.domain.model.Library

data class AddMovieUiState(
    val url: String = "",
    val isFetching: Boolean = false,
    val isSaving: Boolean = false,
    val title: String? = null,
    val description: String? = null,
    val posterUrl: String? = null,
    val libraries: List<Library> = emptyList(),
    val selectedLibraryId: Long? = null,
    val error: String? = null,
    val closeAfterSave: Boolean = false
)
