package com.example.skillcinema.data.repository

import com.example.skillcinema.data.Movie
import com.example.skillcinema.network.MovieApiService
import javax.inject.Inject
import javax.inject.Singleton

interface MovieRepository {
    suspend fun getTopMovies(page: Int): List<Movie>
}

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {
    override suspend fun getTopMovies(page: Int): List<Movie> {
        return runCatching {
            apiService.getTopMovies(page).films
        }.getOrElse {
            emptyList()
        }
    }
}