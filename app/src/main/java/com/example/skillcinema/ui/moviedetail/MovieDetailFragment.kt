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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.GlideApp
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.Actor
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.ActorResponse
import com.example.skillcinema.databinding.FragmentMovieDetailBinding
import com.example.skillcinema.ui.adapters.ActorsAdapter
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

    private val actorsAdapter = ActorsAdapter { actorId ->
        // Обработка клика по актеру
    }

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
        setupActorsRecyclerView()
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

    private fun setupActorsRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.HORIZONTAL, false)
        binding.rvActors.layoutManager = layoutManager
        binding.rvActors.adapter = actorsAdapter
    }

    private fun observeStates() {
        observeMovieDetails()
        observeActorsList()
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

    private fun observeActorsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actorsList.collectLatest { actors ->
                    updateActorsUI(actors)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateActorsUI(actors: List<ActorResponse>) {
        // Общее количество актёров
        val totalActors = actors.size

        // Отображаем только 20 первых актёров, если их больше
        val actorList = actors.take(20).map { actor ->
            Actor(
                id = actor.staffId,
                name = actor.nameRu ?: "Неизвестный актер",
                role = actor.description ?: "Роль не указана",
                photoUrl = actor.posterUrl,
                profession = actor.professionKey
            )
        }

        // Обновляем адаптер
        actorsAdapter.submitList(actorList)
        binding.rvActors.visibility = View.VISIBLE

        // Добавляем информацию о количестве актёров
        binding.tvActorsCount.text = if (totalActors > 20) {
            "$totalActors >"
        } else {
            ""
        }
    }

    private fun updateUI(movie: MovieDetailResponse) {
        with(binding) {
            GlideApp.with(this@MovieDetailFragment)
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivMoviePoster)

            tvMovieTitle.text = movie.nameRu
            tvMovieOriginalTitle.text = movie.nameOriginal ?: ""
            tvMovieRating.text = movie.ratingKinopoisk?.toString() ?: "N/A"
            tvMovieYearGenres.text = formatYearAndGenres(movie)
            tvCountry.text = movie.countries.joinToString(", ") { it.country }
            tvDuration.text = movie.filmLength?.let { "$it мин." } ?: "Неизвестно"
            tvAgeRestrictions.text = movie.ratingAgeLimits?.let { "$it+" } ?: "Не указано"
            // Обрезка описания до 250 символов
            val fullDescription = movie.description?.takeIf { it.isNotBlank() } ?: "Описание не доступно"
            val shortDescription = if (fullDescription.length > 250) {
                fullDescription.substring(0, 250) + "..."
            } else {
                fullDescription
            }
            tvMovieDescription.text = shortDescription
            tvMovieDescription.visibility = View.VISIBLE // Убедимся, что элемент не скрыт

            // Добавление обработчика нажатий для разворачивания текста
            var isExpanded = false
            tvMovieDescription.setOnClickListener {
                isExpanded = !isExpanded
                tvMovieDescription.text = if (isExpanded) fullDescription else shortDescription
                tvMovieDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 5
            }
        }
    }

    private fun formatYearAndGenres(movie: MovieDetailResponse): String {
        return listOfNotNull(
            movie.year?.toString(),
            movie.genres?.joinToString { it.genre }
        ).joinToString(" | ")
    }

    private fun showErrorAndExit(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE
    }

    private fun fetchMovieDetailsWithRetry(retryCount: Int = 3) {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("MovieDetailFragment", "Начинаем загрузку данных для фильма ID: $movieId")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}