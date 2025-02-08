package com.example.skillcinema.network

import com.example.skillcinema.data.MoviesByGenreAndCountryResponse
import com.example.skillcinema.data.PremieresResponse
import com.example.skillcinema.data.MovieCollectionResponse
import com.example.skillcinema.data.TopMoviesResponse
import com.example.skillcinema.data.TvSeriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("/api/v2.2/films/top")
    suspend fun getTopMovies(
        @Query("type") type: String,
        @Query("page") page: Int
    ): TopMoviesResponse

    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Query("year") year: Int,
        @Query("month") month: String
    ): PremieresResponse

    @GET("/api/v2.2/films")
    suspend fun getMoviesByGenreAndCountry(
        @Query("countries") countryId: Int,
//        @Query("genres") genreId: Int,
        @Query("ratingFrom") ratingFrom: Int = 8,
        @Query("page") page: Int = 1
    ): MoviesByGenreAndCountryResponse

    @GET("/api/v2.2/films/collections")
    suspend fun getTop250Movies(
        @Query("type") type: String = "TOP_250_MOVIES",
        @Query("page") page: Int = 1
    ): MovieCollectionResponse

    @GET("/api/v2.2/films")
    suspend fun getTV_SERIES(
//        @Query("order") order: String ="RATING",
        @Query("type") type: String = "TV_SERIES",
//        @Query("ratingFrom") ratingFrom: Number = 0,
//        @Query("ratingTo") ratingTo: Number =10,
//        @Query("yearFrom") yearFrom: Int = 1000,
//        @Query("yearTo") yearTo: Int = 3000,
        @Query("page") page: Int = 1
    ): TvSeriesResponse


    @GET("/api/v2.2/films/filters")
    suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse
}