package com.example.skillcinema.network

import com.example.skillcinema.data.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("/api/v2.2/films/collections")
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
        @Query("genres") genreId: Int,
        @Query("ratingFrom") ratingFrom: Int = 8,
        @Query("page") page: Int = 1
    ): MoviesByGenreAndCountryResponse

    @GET("/api/v2.2/films/collections")
    suspend fun getTop250Movies(
        @Query("type") type: String = "TOP_250_MOVIES",
        @Query("page") page: Int = 1
    ): MovieCollectionResponse

    @GET("/api/v2.2/films")
    suspend fun getTvSeries(
        @Query("type") type: String = "TV_SERIES",
        @Query("page") page: Int = 1
    ): TvSeriesResponse

    @GET("/api/v2.2/films/filters")
    suspend fun getAvailableGenresAndCountries(): GenresAndCountriesResponse

    @GET("/api/v2.2/films/{filmId}")
    suspend fun getMovieDetails(@Path("filmId") filmId: Int): MovieDetailResponse

    @GET("/api/v2.2/films/{filmId}/seasons")
    suspend fun getSeasons(@Path("filmId") filmId: Int): SeasonsResponse

    @GET("/api/v2.2/films/{filmId}/frames")
    suspend fun getMovieGallery(@Path("filmId") filmId: Int): GalleryResponse

    @GET("/api/v2.2/films/{filmId}/similars")
    suspend fun getSimilarMovies(@Path("filmId") filmId: Int): SimilarMoviesResponse

    @GET("/api/v1/staff")
    suspend fun getMovieCast(@Query("filmId") filmId: Int): List<ActorResponse>

    @GET("search")
    suspend fun searchMovies(@Query("query") query: String): MovieResponse

}