package com.example.watchit.data.local

import android.content.Context
import androidx.room.Room


object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase{
        return INSTANCE ?: synchronized(this){
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "movie_db.db"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}