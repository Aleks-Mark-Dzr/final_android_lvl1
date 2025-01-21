package com.example.skillcinema

import android.app.Application
import com.example.skillcinema.data.repository.MovieRepository
import com.example.skillcinema.data.repository.MovieRepositoryImpl
import com.example.skillcinema.network.MovieApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SkillCinemaApp : Application() {

    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()

        // Настройка OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-API-KEY", "f08ba7c2-5719-4c08-9888-764c3e4954f5")
                    .build()
                chain.proceed(request)
            }
            .build()

        // Настройка Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kinopoiskapiunofficial.tech/api/v2.2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val movieApiService = retrofit.create(MovieApiService::class.java)

        // Инициализация репозитория
        movieRepository = MovieRepositoryImpl(movieApiService)
    }
}