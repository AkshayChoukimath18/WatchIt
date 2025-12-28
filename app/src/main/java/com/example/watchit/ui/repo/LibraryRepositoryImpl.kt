package com.example.watchit.ui.repo

import android.util.Log
import android.widget.Toast
import com.example.watchit.data.local.LibraryDao
import com.example.watchit.data.local.LibraryEntity
import com.example.watchit.data.local.MovieEntity
import com.example.watchit.data.remote.LinkPreview
import com.example.watchit.data.remote.LinkPreviewFetcher
import com.example.watchit.domain.model.Library
import com.example.watchit.domain.model.LibraryVisibility
import com.example.watchit.domain.model.LibraryWithMoviesDomain
import com.example.watchit.domain.model.toDomain
import com.example.watchit.util.detectPlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.PrivateKey
import javax.inject.Inject


class LibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao,
    private val linkPreviewFetcher: LinkPreviewFetcher,
): LibraryRepository {
    override fun getLibraries(): Flow<List<Library>> =
        dao.getLibraries().map { list ->
            list.map { it.toDomain() }
        }


    override fun getLibraryWithMovies(libraryId: Long): Flow<LibraryWithMoviesDomain?> =
        dao.getLibraryWithMovies(libraryId).map { it?.toDomain() }

    override suspend fun createLibrary(
        name: String,
        description: String?,
        visibility: LibraryVisibility
    ): Long {
        val entity = LibraryEntity(
            name = name,
            description = description,
            visibility = visibility.name
        )
        return  dao.insertLibrary(entity)
    }

    override suspend fun addMovieToLibrary(
        libraryId: Long,
        title: String,
        posterUrl: String?,
        sourceUrl: String,
        sourcePlatform: String
    ): Long {
        val entity = MovieEntity(
            title = title,
            posterUrl = posterUrl,
            sourceUrl = sourceUrl,
            sourcePlatform = sourcePlatform
        )
        return dao.addMovieToLibrary(libraryId, entity)
    }

    override suspend fun removeMovieFromLibrary(libraryId: Long, movieId: Long) {
        dao.removeMovieFromLibrary(libraryId, movieId)
    }

    override suspend fun addMovieFromUrl(
        libraryId: Long,
        url: String
    ): Result<Unit> {
        return try{
            val preview = linkPreviewFetcher.fetch(url)
                ?: return Result.failure(IllegalStateException("Can not fetch preview"))

            val title = preview.title?: "Untitled"
            val imageUrl = preview.imageUrl
            val platform = detectPlatform(url)

            addMovieToLibrary(
                libraryId = libraryId,
                title = title,
                posterUrl = imageUrl,
                sourceUrl = url,
                sourcePlatform = platform,
            )

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun previewLink(url: String): Result<LinkPreview> {
        return try {
            val preview = linkPreviewFetcher.fetch(url)
                ?: return Result.failure(IllegalStateException("Can not fetch preview"))
            Result.success(preview)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getAllLibraryWithMovies(): Flow<List<LibraryWithMoviesDomain>> {
        return dao.getAllLibraryWithMovies().map { list ->
            list.map { it.toDomain() }
        }
    }



}