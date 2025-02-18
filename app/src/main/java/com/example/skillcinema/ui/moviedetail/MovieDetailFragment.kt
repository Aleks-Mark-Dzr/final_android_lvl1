package com.example.skillcinema.ui.moviedetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.databinding.FragmentMovieDetailBinding
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModel
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailViewModel
    private var movieId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("movieId") ?: 0

        Log.d("MovieDetailFragment", "🎬 Получен movieId: $movieId")

        if (movieId == 0) {
            Toast.makeText(requireContext(), "❌ Ошибка: ID фильма не найден!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
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

        Log.d("MovieDetailFragment", "📤 Запрашиваем детали фильма с ID: $movieId")

        fetchMovieDetailsWithRetry()
        observeMovieDetails()
        observeFavoriteState()
        observeWatchedState()

        binding.btnFavorite.setOnClickListener { viewModel.toggleFavorite(movieId) }
        binding.btnWatched.setOnClickListener { viewModel.toggleWatched(movieId) }
    }

    private fun observeMovieDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetail.collectLatest { movie ->
                    if (movie != null) {
                        Log.d("MovieDetailFragment", "✅ Фильм загружен: ${movie.nameRu}")
                        updateUI(movie)
                        binding.progressBar.visibility = View.GONE
                        binding.contentContainer.visibility = View.VISIBLE
                    } else {
                        Log.w("MovieDetailFragment", "⏳ Данные фильма пока не загружены...")
                        binding.progressBar.visibility = View.VISIBLE
                        binding.contentContainer.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collectLatest { isFavorite ->
                    binding.btnFavorite.text = if (isFavorite) "Удалить из любимых" else "Добавить в любимые"
                }
            }
        }
    }

    private fun observeWatchedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isWatched.collectLatest { isWatched ->
                    binding.btnWatched.text = if (isWatched) "Уже просмотрено" else "Добавить в просмотренные"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(movie: MovieDetailResponse) {
        binding.apply {
            tvMovieTitle.text = movie.nameRu
            tvMovieOriginalTitle.text = movie.nameOriginal ?: ""
            tvMovieYearGenres.text = "${movie.year} | ${movie.genres.joinToString { it.genre }}"
        }
    }

    private fun fetchMovieDetailsWithRetry(retryCount: Int = 3) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeat(retryCount) { attempt ->
                Log.d("MovieDetailFragment", "📤 Попытка загрузки фильма (попытка ${attempt + 1})")

                try {
                    viewModel.fetchMovieDetails(movieId)
                    delay(1000)

                    if (viewModel.movieDetail.value != null) {
                        Log.d("MovieDetailFragment", "✅ Фильм загружен с попытки ${attempt + 1}")
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e("MovieDetailFragment", "❌ Ошибка загрузки фильма: ${e.message}", e)
                }
            }

            // Если фильм так и не загрузился, закрываем экран
            Log.e("MovieDetailFragment", "❌ Фильм не удалось загрузить после $retryCount попыток")
            Toast.makeText(requireContext(), "Ошибка загрузки фильма", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}