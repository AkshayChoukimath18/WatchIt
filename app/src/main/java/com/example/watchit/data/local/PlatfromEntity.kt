package com.example.watchit.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "platforms",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["movieId", "sourcePlatform"], unique = true)
    ]
)
data class PlatfromEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val movieId: Long,
    val sourcePlatform: String,
    val soruceUrl: String,
    val posterUrl: String?
)
