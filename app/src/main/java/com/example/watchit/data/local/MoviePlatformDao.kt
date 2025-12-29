package com.example.watchit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MoviePlatformDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlatform(entity: PlatfromEntity)
}