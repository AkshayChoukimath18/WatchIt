package com.example.watchit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.watchit.domain.model.LibraryWithMoviesDomain
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(libraryEntity: LibraryEntity): Long

    @Update
    suspend fun updateLibrary(libraryEntity: LibraryEntity)

    @Delete
    suspend fun deleteLibrary(libraryEntity: LibraryEntity)

    @Query("SELECT * FROM libraries ORDER BY createdAt desc")
    fun getLibraries(): Flow<List<LibraryEntity>>

    @Transaction
    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    fun getLibraryWithMovies(libraryId: Long): Flow<LibraryWithMovies?>


    @Query("SELECT * FROM libraries")
    fun getAllLibraryWithMovies(): Flow<List<LibraryWithMovies>>




    // --- Movie ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieEntity: MovieEntity): Long

    @Query("SELECT * FROM movies WHERE id= :movieId")
    fun getMovieById(movieId: Long): MovieEntity?

    @Query("SELECT id FROM movies WHERE sourceUrl = :url LIMIT 1")
    suspend fun getMovieIdBySourceUrl(url: String): Long


    // --- Linking movies to libraries ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLibraryMovieCrossRef(crossRef: LibraryMovieCrossRef):Long

    @Transaction
    suspend fun addMovieToLibrary(libraryId: Long, movie: MovieEntity): Boolean {

        val movieId = insertMovie(movie).takeIf { it != -1L } ?: getMovieIdBySourceUrl(movie.sourceUrl)
        val result = insertLibraryMovieCrossRef(
            LibraryMovieCrossRef(
                libraryId = libraryId,
                movieId = movieId
            )
        )
        return result != -1L
    }

    @Query("""
        DELETE FROM library_movies WHERE libraryId= :libraryId AND movieId= :movieId
        """)
    suspend fun removeMovieFromLibrary(libraryId: Long, movieId: Long)


}