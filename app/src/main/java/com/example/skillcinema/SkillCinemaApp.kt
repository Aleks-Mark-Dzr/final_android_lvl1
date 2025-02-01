package com.example.skillcinema

import android.app.Application
import com.example.skillcinema.data.repository.MovieRepository
import com.example.skillcinema.data.repository.MovieRepositoryImpl
import com.example.skillcinema.network.CustomHttpLoggingInterceptor // Импорт кастомного Interceptor
import com.example.skillcinema.network.MovieApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SkillCinemaApp : Application() {

    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()

        // 1. Создаем экземпляр кастомного Interceptor
        val customLoggingInterceptor = CustomHttpLoggingInterceptor()

        // 2. Настройка OkHttpClient с кастомным Interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-API-KEY", "f08ba7c2-5719-4c08-9888-764c3e4954f5") // Добавляем API-ключ
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(customLoggingInterceptor) // Добавляем кастомный Interceptor
            .build()

        // 3. Настройка Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kinopoiskapiunofficial.tech/api/v2.2/") // Базовый URL API
            .addConverterFactory(GsonConverterFactory.create()) // Конвертер для JSON
            .client(okHttpClient) // Используем настроенный OkHttpClient
            .build()

        // 4. Создаем сервис для работы с API
        val movieApiService = retrofit.create(MovieApiService::class.java)

        // 5. Инициализация репозитория
        movieRepository = MovieRepositoryImpl(movieApiService)
    }
}