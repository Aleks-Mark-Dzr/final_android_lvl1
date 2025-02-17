package com.example.skillcinema.ui.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentMovieDetailBinding
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModel
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModelFactory
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailViewModel
    private var movieId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("movieId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = MovieDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieDetailViewModel::class.java]

        viewModel.fetchMovieDetails(movieId)

        lifecycleScope.launch {
            viewModel.movieDetail.collect { movie ->
                movie?.let {
                    binding.tvMovieTitle.text = it.nameRu
                    binding.tvMovieOriginalTitle.text = it.nameOriginal ?: ""
                    binding.tvMovieYearGenres.text = it.year
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.btnFavorite.text = if (isFavorite) "Удалить из любимых" else "Добавить в любимые"
            }
        }

        lifecycleScope.launch {
            viewModel.isWatched.collect { isWatched ->
                binding.btnWatched.text = if (isWatched) "Уже просмотрено" else "Добавить в просмотренные"
            }
        }

        binding.btnFavorite.setOnClickListener { viewModel.toggleFavorite(movieId) }
        binding.btnWatched.setOnClickListener { viewModel.toggleWatched(movieId) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}