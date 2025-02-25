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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.GlideApp
import com.example.skillcinema.R
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

        if (movieId == 0) {
            showErrorAndExit("❌ Ошибка: ID фильма не найден!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupClickListeners()
        fetchMovieDetailsWithRetry()
        observeStates()
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = MovieDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieDetailViewModel::class.java]
    }

    private fun setupClickListeners() {
        binding.ivFavorite.setOnClickListener {
            viewModel.toggleFavorite(movieId)
        }
        binding.ivWatchLater.setOnClickListener {
            viewModel.toggleWatchLater(movieId)
        }
        binding.ivWatched.setOnClickListener {
            viewModel.toggleWatched(movieId)
        }
    }

    private fun observeStates() {
        observeMovieDetails()
        observeFavoriteState()
        observeWatchLaterState()
        observeWatchedState()
    }

    private fun observeMovieDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetail.collectLatest { movie ->
                    movie?.let {
                        updateUI(it)
                        showContent()
                    } ?: showLoading()
                }
            }
        }
    }

    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collectLatest { isFavorite ->
                    val icon = if (isFavorite) R.drawable.ic_favorite_2
                    else R.drawable.ic_no_favorite_2
                    binding.ivFavorite.setImageResource(icon)
                }
            }
        }
    }

    private fun observeWatchLaterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isWatchLater.collectLatest { isWatchLater ->
                    val icon = if (isWatchLater) R.drawable.ic_bookmark_2
                    else R.drawable.ic_no_bookmark
                    binding.ivWatchLater.setImageResource(icon)
                }
            }
        }
    }

    private fun observeWatchedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isWatched.collectLatest { isWatched ->
                    val icon = if (isWatched) R.drawable.ic_viewed
                    else R.drawable.ic_not_viewed_2
                    binding.ivWatched.setImageResource(icon)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(movie: MovieDetailResponse) {
        with(binding) {
            tvMovieTitle.text = movie.nameRu
            tvMovieOriginalTitle.text = movie.nameOriginal ?: ""
            tvMovieRating.text = movie.ratingKinopoisk?.toString() ?: "N/A"
            tvMovieYearGenres.text = formatYearAndGenres(movie)
            loadPoster(movie.posterUrl)
        }
    }

    private fun formatYearAndGenres(movie: MovieDetailResponse): String {
        return listOfNotNull(
            movie.year?.toString(),
            movie.genres?.joinToString { it.genre }
        ).joinToString(" | ")
    }

    private fun loadPoster(posterUrl: String?) {
        if (posterUrl.isNullOrEmpty()) {
            showPosterError()
            return
        }

        GlideApp.with(this)
            .load(posterUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivMoviePoster)
    }

    private fun fetchMovieDetailsWithRetry(retryCount: Int = 3) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeat(retryCount) { attempt ->
                try {
                    viewModel.fetchMovieDetails(movieId)
                    delay(1000)
                    if (viewModel.movieDetail.value != null) return@launch
                } catch (e: Exception) {
                    Log.e("MovieDetailFragment", "Attempt ${attempt + 1} failed: ${e.message}")
                }
            }
            showErrorAndExit("❌ Ошибка загрузки фильма")
        }
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE
    }

    private fun showPosterError() {
        Log.e("MovieDetailFragment", "Poster URL is invalid")
        binding.ivMoviePoster.setImageResource(R.drawable.error_image)
    }

    private fun showErrorAndExit(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}