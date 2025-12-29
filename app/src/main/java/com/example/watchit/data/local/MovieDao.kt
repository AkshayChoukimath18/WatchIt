package com.example.watchit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movieEntity: MovieEntity): Long

    @Query("SELECT * FROM movies WHERE id= :movieId")
    fun getMovieById(movieId: Long): MovieEntity?

    @Query("SELECT id FROM movies WHERE title = :title LIMIT 1 ")
    suspend fun getMovieIdByTitle(title: String): Long?
}