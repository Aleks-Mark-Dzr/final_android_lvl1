package com.example.skillcinema.di

import com.example.skillcinema.data.repository.MovieRepository
import com.example.skillcinema.data.repository.MovieRepositoryImpl
import com.example.skillcinema.network.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Добавляем эту аннотацию
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(apiService: MovieApiService): MovieRepository {
        return MovieRepositoryImpl(apiService)
    }
}