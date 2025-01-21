package com.example.skillcinema.network

import com.example.skillcinema.data.TopMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MovieApiService {
//    @Headers("X-API-KEY: $API_KEY")
    @GET("/api/v2.2/films/top?type=TOP_250_BEST_FILMS")
    suspend fun getTopMovies(@Query("page") page: Int): TopMoviesResponse

    companion object {
        private const val API_KEY = "f08ba7c2-5719-4c08-9888-764c3e4954f5"
    }
}