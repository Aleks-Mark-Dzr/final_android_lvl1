package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.Movie
import com.example.skillcinema.network.GenresAndCountriesResponse
import com.example.skillcinema.network.MovieApiService

interface MovieRepository {
    suspend fun getTopMovies(page: Int): List<Movie>
    suspend fun getPremieres(year: Int, month: String): List<Movie>
    suspend fun getMoviesByGenreAndCountry(countryId: Int, genreId: Int): List<Movie>
    suspend fun getTop250Movies(page: Int): List<Movie>
    suspend fun getTvSeries(page: Int): List<Movie>
    suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse
    suspend fun searchMovies(query: String): List<Movie>
}

class MovieRepositoryImpl(
    private val apiService: MovieApiService
) : MovieRepository {

    /**
     * Универсальный метод для выполнения API-запросов с обработкой ошибок.
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T? {
        return try {
            apiCall()
        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "API Error: ${e.message}", e)
            null
        }
    }

    override suspend fun getPremieres(year: Int, month: String): List<Movie> =
        safeApiCall { apiService.getPremieres(year, month).items } ?: emptyList()

    override suspend fun getTopMovies(page: Int): List<Movie> =
        safeApiCall { apiService.getTopMovies(type = "TOP_POPULAR_ALL", page).items } ?: emptyList()

    override suspend fun getMoviesByGenreAndCountry(countryId: Int, genreId: Int): List<Movie> =
        safeApiCall { apiService.getMoviesByGenreAndCountry(countryId, genreId).items } ?: emptyList()

    override suspend fun getTop250Movies(page: Int): List<Movie> =
        safeApiCall { apiService.getTop250Movies(page = page).items } ?: emptyList()

    override suspend fun getTvSeries(page: Int): List<Movie> =
        safeApiCall { apiService.getTvSeries(page = page).items } ?: emptyList()

    override suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse =
        safeApiCall { apiService.getAvailableGenresAndCountries() } ?: GenresAndCountriesResponse(emptyList(), emptyList())

    override suspend fun searchMovies(query: String): List<Movie> {
        Log.d("MovieRepository", "API запрос: $query") // Логируем API-запрос
        return safeApiCall { apiService.searchMovies(query = query).items } ?: emptyList()
    }
}