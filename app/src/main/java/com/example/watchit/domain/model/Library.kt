package com.example.watchit.domain.model

data class Library(
    val id: Long,
    val name: String,
    val description: String?,
    val visibility: LibraryVisibility,
    val createdAt: Long
)

enum class LibraryVisibility{
    PRIVATE, PUBLIC, SHARED
}
