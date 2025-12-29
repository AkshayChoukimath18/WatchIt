package com.example.watchit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    LibraryEntity::class,
    MovieEntity::class,
    LibraryMovieCrossRef::class
], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
}