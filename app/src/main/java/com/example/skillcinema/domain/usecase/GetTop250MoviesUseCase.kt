package com.example.skillcinema.domain.usecase

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository

class GetTop250MoviesUseCase(private val repository: MovieRepository) {
    suspend fun execute(page: Int): List<Movie> {
        return repository.getTop250Movies(page)
    }
}