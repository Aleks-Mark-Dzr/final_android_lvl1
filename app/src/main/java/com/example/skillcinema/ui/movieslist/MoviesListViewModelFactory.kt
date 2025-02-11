package com.example.skillcinema.ui.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.domain.usecase.*

class MoviesListViewModelFactory(
    private val getPremieresUseCase: GetPremieresUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getMoviesByGenreAndCountryUseCase: GetMoviesByGenreAndCountryUseCase,
    private val getTop250MoviesUseCase: GetTop250MoviesUseCase,
    private val getTvSeriesUseCase: GetTvSeriesUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesListViewModel(
                getPremieresUseCase,
                getPopularMoviesUseCase,
                getMoviesByGenreAndCountryUseCase,
                getTop250MoviesUseCase,
                getTvSeriesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}