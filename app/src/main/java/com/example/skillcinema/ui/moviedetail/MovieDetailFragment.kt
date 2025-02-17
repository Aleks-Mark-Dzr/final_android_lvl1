package com.example.skillcinema.ui.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        if (movieId == 0) {
            Toast.makeText(requireContext(), "Ошибка: ID фильма не найден!", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed() // Закрываем экран, если ID некорректный
        }
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.movieDetail.collect { movie ->
                    if (movie != null) {
                        binding.tvMovieTitle.text = movie.nameRu
                        binding.tvMovieOriginalTitle.text = movie.nameOriginal ?: ""
                        binding.tvMovieYearGenres.text = "${movie.year} | ${movie.genres.joinToString { it.genre }}"
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки фильма", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.isFavorite.collect { isFavorite ->
                    binding.btnFavorite.text = if (isFavorite) "Удалить из любимых" else "Добавить в любимые"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.isWatched.collect { isWatched ->
                    binding.btnWatched.text = if (isWatched) "Уже просмотрено" else "Добавить в просмотренные"
                }
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