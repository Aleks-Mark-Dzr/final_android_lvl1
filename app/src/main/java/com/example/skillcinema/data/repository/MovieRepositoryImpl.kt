package com.example.skillcinema.data.repository

import com.example.skillcinema.data.Movie
import com.example.skillcinema.network.MovieApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository
 {

    override suspend fun getTopMovies(page: Int): List<Movie> {
        return try {
            val response = apiService.getTopMovies(page)
            response.films // Возвращаем список фильмов из API
        } catch (e: Exception) {
            // Логируем ошибку или обрабатываем её, возвращаем пустой список
            emptyList()
        }
    }
}