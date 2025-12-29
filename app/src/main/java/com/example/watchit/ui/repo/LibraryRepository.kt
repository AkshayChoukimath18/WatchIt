package com.example.watchit.ui.repo

import com.example.watchit.data.local.LibraryWithMovies
import com.example.watchit.data.remote.LinkPreview
import com.example.watchit.domain.model.Library
import com.example.watchit.domain.model.LibraryVisibility
import com.example.watchit.domain.model.LibraryWithMoviesDomain
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraries(): Flow<List<Library>>

    fun getLibraryWithMovies(libraryId: Long): Flow<LibraryWithMoviesDomain?>

    suspend fun createLibrary(
        name: String,
        description: String?,
        visibility: LibraryVisibility = LibraryVisibility.PRIVATE
    ): Long

    suspend fun addMovieToLibrary(
        libraryId: Long,
        title: String,
        posterUrl: String?,
        sourceUrl: String,
        sourcePlatform: String
    ): Boolean

    suspend fun removeMovieFromLibrary(libraryId: Long, movieId: Long)

    suspend fun addMovieFromUrl(libraryId: Long, url: String): Result<Unit>

    suspend fun previewLink(url: String): Result<LinkPreview>

    fun getAllLibraryWithMovies(): Flow<List<LibraryWithMoviesDomain>>

}