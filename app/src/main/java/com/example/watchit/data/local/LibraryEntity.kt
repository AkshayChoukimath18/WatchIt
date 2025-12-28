package com.example.watchit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libraries")
data class LibraryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val visibility: String = "PRIVATE",
    val createdAt: Long = System.currentTimeMillis()
)
