package com.example.skillcinema.domain.usecase

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository

class GetTvSeriesUseCase(private val repository: MovieRepository) {
    suspend fun execute(page: Int): List<Movie> {
        return repository.getTvSeries(page)
    }
}