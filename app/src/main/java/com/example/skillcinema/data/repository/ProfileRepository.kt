package com.example.skillcinema.data.repository

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.database.MovieDao
import com.example.skillcinema.data.database.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ProfileRepository {
    fun getFavoriteMovies(): Flow<List<Movie>>
}

class ProfileRepositoryImpl(
    private val movieDao: MovieDao
) : ProfileRepository {

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    private fun MovieEntity.toMovie(): Movie {
        return Movie(
            filmId = movieId,
            kinopoiskId = movieId,
            nameRu = nameRu,
            year = year ?: "",
            posterUrlPreview = posterUrl ?: "",
            rating = rating,
            ratingKinopoisk = rating,
            genres = emptyList()
        )
    }
}