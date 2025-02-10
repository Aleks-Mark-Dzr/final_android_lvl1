package com.example.skillcinema.domain.usecase

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository

class GetPremieresUseCase(private val repository: MovieRepository) {
    suspend fun execute(year: Int, month: String): List<Movie> {
        return repository.getPremieres(year, month)
    }
}