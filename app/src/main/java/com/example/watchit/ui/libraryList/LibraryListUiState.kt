package com.example.watchit.ui.libraryList

import com.example.watchit.domain.model.Library

data class LibraryListUiState(
    val isLoading: Boolean = false,
    val libraries: List<Library> = emptyList(),
    val error: String? = null
)
