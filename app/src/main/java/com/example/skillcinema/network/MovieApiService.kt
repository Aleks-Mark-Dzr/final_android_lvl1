package com.example.skillcinema.network

import android.graphics.Movie
import androidx.contentpager.content.Query
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query

interface MovieApiService {

//    @GET("/api/v2.2/films/top")
//    suspend fun getTopMovies(@Query("page") page: Int): List<Movie>

    companion object {
        private const val BASE_URL = "https://kinopoiskapiunofficial.tech"

//        fun create(): MovieApiService {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(MoshiConverterFactory.create())
//                .build()
//                .create(MovieApiService::class.java)
//        }
    }
}