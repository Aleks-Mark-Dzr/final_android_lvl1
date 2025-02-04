package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.Movie
import com.example.skillcinema.network.GenresAndCountriesResponse
import com.example.skillcinema.network.MovieApiService
import javax.inject.Inject
import javax.inject.Singleton

interface MovieRepository {
    suspend fun getTopMovies(page: Int): List<Movie>
    suspend fun getPremieres(year: Int, month: String): List<Movie>
    suspend fun getMoviesByGenreAndCountry(countryId: Int, genreId: Int): List<Movie>
    suspend fun getTop250Movies(page: Int): List<Movie>
    suspend fun getTvSeries(
        page: Int
    ): List<Movie>
    suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse
}

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {

    override suspend fun getPremieres(year: Int, month: String): List<Movie> {
        return runCatching {
            val response = apiService.getPremieres(year, month)
            Log.d("MovieRepositoryImpl", "Premieres response: ${response.items}")
            response.items.shuffled()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching premieres: ${it.message}", it)
            emptyList()
        }
    }

    override suspend fun getTopMovies(page: Int): List<Movie> {
        return runCatching {
            val response = apiService.getTopMovies(type = "TOP_250_BEST_FILMS", page)
            Log.d("MovieRepositoryImpl", "Top movies response: ${response.films}")
            response.films.shuffled()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching top movies: ${it.message}", it)
            emptyList()
        }
    }

    override suspend fun getMoviesByGenreAndCountry(countryId: Int, genreId: Int): List<Movie> {
        return runCatching {
            val response = apiService.getMoviesByGenreAndCountry(countryId, genreId)
            Log.d("MovieRepositoryImpl", "Dynamic category response: ${response.items}")
            response.items.shuffled()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching dynamic category: ${it.message}", it)
            emptyList()
        }
    }

    override suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse {
        return runCatching {
            apiService.getAvailableGenresAndCountries()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching genres and countries: ${it.message}", it)
            GenresAndCountriesResponse(emptyList(), emptyList())
        }
    }

    override suspend fun getTop250Movies(page: Int): List<Movie> {
        return runCatching {
            val response = apiService.getTop250Movies(type = "TOP_250_MOVIES", page)
            Log.d("MovieRepositoryImpl", "Top-250 movies response: ${response.items}")
            response.items.shuffled()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching top movies: ${it.message}", it)
            emptyList()
        }
    }

    override suspend fun getTvSeries(
        page: Int
    ): List<Movie> {
        return runCatching {
            val response = apiService.getTV_SERIES(type = "TV_SERIES", page)
            Log.d("MovieRepositoryImpl", "Top TV_SERIES response: ${response.items}")
            response.items.shuffled()
        }.getOrElse {
            Log.e("MovieRepositoryImpl", "Error fetching top movies: ${it.message}", it)
            emptyList()
        }
    }
}