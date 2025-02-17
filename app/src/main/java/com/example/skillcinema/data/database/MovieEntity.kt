package com.example.skillcinema.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val movieId: Int,
    val nameRu: String,
    val nameOriginal: String?,
    val year: String?,
    val posterUrl: String?,
    val rating: Double?,
    val isWatched: Boolean = false,
    val isFavorite: Boolean = false
)