package com.example.skillcinema.ui.moviedetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.GlideApp
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.Actor
import com.example.skillcinema.data.ActorResponse
import com.example.skillcinema.data.CrewMember
import com.example.skillcinema.data.GalleryItem
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.databinding.FragmentMovieDetailBinding
import com.example.skillcinema.ui.adapters.ActorsAdapter
import com.example.skillcinema.ui.adapters.CrewAdapter
import com.example.skillcinema.ui.moviedetail.SimilarMoviesAdapter
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModel
import com.example.skillcinema.ui.moviedetail.viewmodel.MovieDetailViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailViewModel
    private var movieId: Int = 0

    private val actorsAdapter = ActorsAdapter { actorId ->
        // Обработка клика по актеру
    }

    private val crewAdapter = CrewAdapter { crewId ->
        // Обработка клика по персоналу
    }

    private val galleryAdapter = GalleryAdapter()

    private val similarMoviesAdapter by lazy {
        SimilarMoviesAdapter { selectedMovieId ->
            navigateToMovieDetail(selectedMovieId)
        }
    }

    private var selectedGalleryType: String? = null

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
        setupCrewRecyclerView()
        setupGalleryRecyclerView()
        setupSimilarMoviesRecyclerView()
        showLoading()
        viewModel.fetchMovieDetails(movieId)
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
        binding.tvActorsCount.setOnClickListener {
            navigateToActorsList()
        }
        binding.tvStaffCount.setOnClickListener {
            navigateToCrewList()
        }
    }

    private fun setupActorsRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.HORIZONTAL, false)
        binding.rvActors.layoutManager = layoutManager
        binding.rvActors.adapter = actorsAdapter
    }

    private fun setupCrewRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
        binding.rvStaff.layoutManager = layoutManager
        binding.rvStaff.adapter = crewAdapter
    }

    private fun setupGalleryRecyclerView() {
        binding.rvPhotos.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvPhotos.adapter = galleryAdapter
    }

    private fun setupSimilarMoviesRecyclerView() {
        binding.rvSimilarMovies.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSimilarMovies.adapter = similarMoviesAdapter
    }

    private fun observeStates() {
        observeMovieDetails()
        observeActorsList()
        observeCrewList()
        observeFavoriteState()
        observeWatchLaterState()
        observeWatchedState()
        observeErrorMessages()
        observeGallery()
        observeSimilarMovies()
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

    private fun observeErrorMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessages.collectLatest { message ->
                    if (message.isNotBlank()) {
                        showErrorAndExit(message)
                    }
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

    private fun observeCrewList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crewList.collectLatest { staff ->
                    updateCrewUI(staff)
                }
            }
        }
    }

    private fun observeGallery() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.galleryByType,
                    viewModel.galleryTotalCount
                ) { galleryByType, totalCount -> galleryByType to totalCount }
                    .collectLatest { (galleryByType, totalCount) ->
                        updateGalleryUI(galleryByType, totalCount)
                    }
            }
        }
    }

    private fun observeSimilarMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.similarMovies.collectLatest { movies ->
                    updateSimilarMoviesUI(movies)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateActorsUI(actors: List<ActorResponse>) {
        val totalActors = actors.size

        val actorList = actors.take(20).map { actor ->
            Actor(
                id = actor.staffId,
                name = actor.nameRu ?: "Неизвестный актер",
                role = actor.description ?: "Роль не указана",
                photoUrl = actor.posterUrl,
                profession = actor.professionKey
            )
        }

        actorsAdapter.submitList(actorList)
        binding.rvActors.visibility = View.VISIBLE

        binding.tvActorsCount.apply {
            text = totalActors.toString()
            isVisible = totalActors > 0
            isEnabled = totalActors > 0
        }
    }

    private fun navigateToActorsList() {
        if (!isAdded || binding.tvActorsCount.text.isNullOrBlank()) return

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        if (findNavController().currentDestination?.id == R.id.movieDetailFragment) {
            findNavController().navigate(
                R.id.action_movieDetailFragment_to_actorsListFragment,
                bundle
            )
        }
    }

    // ОСТАВЛЕННАЯ ЕДИНСТВЕННАЯ ВЕРСИЯ
    private fun navigateToCrewList() {
        val crewCount = binding.tvStaffCount.text.toString().toIntOrNull()
        if (!isAdded || crewCount == null || crewCount <= 0) return

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        if (findNavController().currentDestination?.id == R.id.movieDetailFragment) {
            findNavController().navigate(
                R.id.action_movieDetailFragment_to_crewListFragment,
                bundle
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCrewUI(staff: List<ActorResponse>) = with(binding) {
        val crew = staff.map { person ->
            CrewMember(
                id = person.staffId,
                name = person.nameRu ?: "Неизвестный участник",
                role = person.profession ?: "",
                photoUrl = person.posterUrl,
                professionText = person.professionText
            )
        }
        val totalCrew = crew.size
        tvStaffCount.apply {
            text = totalCrew.toString()
            isVisible = totalCrew > 0
            isEnabled = totalCrew > 0
        }
        crewAdapter.submitList(if (totalCrew > 6) crew.take(6) else crew)
        rvStaff.visibility = View.VISIBLE
    }

    private fun updateGalleryUI(
        galleryByType: Map<String, List<GalleryItem>>,
        totalCount: Int
    ) = with(binding) {
        if (galleryByType.isEmpty()) {
            chipsScrollContainer.visibility = View.GONE
            rvPhotos.visibility = View.GONE
            tvPhotosCount.text = ""
            tvPhotosCount.visibility = View.GONE
            galleryAdapter.submitList(emptyList())
            return@with
        }

        chipsScrollContainer.visibility = View.VISIBLE
        rvPhotos.visibility = View.VISIBLE
        val shouldShowCount = totalCount > 20
        tvPhotosCount.text = if (shouldShowCount) totalCount.toString() else ""
        tvPhotosCount.isVisible = shouldShowCount

        createOrUpdateChips(galleryByType)
        updatePhotosList(galleryByType)
    }

    private fun createOrUpdateChips(galleryByType: Map<String, List<GalleryItem>>) {
        val chipGroup = binding.chipGroupPhotoTypes
        val sortedTypes = galleryByType.keys.sortedBy { getGalleryTypeName(it) }

        val desiredSelection = when {
            selectedGalleryType != null && galleryByType.containsKey(selectedGalleryType) ->
                selectedGalleryType
            else -> sortedTypes.firstOrNull()
        }
        selectedGalleryType = desiredSelection

        chipGroup.removeAllViews()

        sortedTypes.forEach { type ->
            val chip = Chip(requireContext()).apply {
                text = getGalleryTypeName(type)
                isCheckable = true
                tag = type
            }
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedGalleryType != type) {
                        selectedGalleryType = type
                        updatePhotosList(galleryByType)
                    }
                }
            }
            chipGroup.addView(chip)
            if (type == selectedGalleryType) {
                chip.isChecked = true
            }
        }

        if (chipGroup.childCount == 0) {
            selectedGalleryType = null
        }
    }

    private fun updatePhotosList(galleryByType: Map<String, List<GalleryItem>>) {
        val type = selectedGalleryType
        val images = if (type != null) {
            galleryByType[type].orEmpty()
        } else {
            emptyList()
        }
        galleryAdapter.submitList(images.take(20))
    }

    private fun getGalleryTypeName(type: String): String {
        return when (type.uppercase()) {
            "SHOOTING" -> getString(R.string.gallery_type_shooting)
            "STILL" -> getString(R.string.gallery_type_still)
            "POSTER" -> getString(R.string.gallery_type_poster)
            "FAN_ART" -> getString(R.string.gallery_type_fan_art)
            "CONCEPT" -> getString(R.string.gallery_type_concept)
            "WALLPAPER" -> getString(R.string.gallery_type_wallpaper)
            "COVER" -> getString(R.string.gallery_type_cover)
            "SCREENSHOT" -> getString(R.string.gallery_type_screenshot)
            else -> getString(R.string.gallery_type_other)
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

            val yearAndGenres = formatYearAndGenres(movie)
            tvMovieYearGenres.text = yearAndGenres
            tvMovieYearGenres.isVisible = yearAndGenres.isNotBlank()

            val countryDurationAge = formatCountryDurationAge(movie)
            tvMovieCountryDurationAge.text = countryDurationAge
            tvMovieCountryDurationAge.isVisible = countryDurationAge.isNotBlank()

            val fullDescription =
                movie.description?.takeIf { it.isNotBlank() } ?: "Описание не доступно"
            val shortDescription = if (fullDescription.length > 250) {
                fullDescription.substring(0, 250) + "..."
            } else {
                fullDescription
            }
            tvMovieDescription.text = shortDescription
            tvMovieDescription.visibility = View.VISIBLE

            var isExpanded = false
            tvMovieDescription.setOnClickListener {
                isExpanded = !isExpanded
                tvMovieDescription.text = if (isExpanded) fullDescription else shortDescription
                tvMovieDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 5
            }
        }
    }

    private fun updateSimilarMoviesUI(movies: List<Movie>) = with(binding) {
        val hasMovies = movies.isNotEmpty()

        similarMoviesContainer.isVisible = hasMovies

        if (!hasMovies) {
            tvSimilaviesCount.text = ""
            tvSimilaviesCount.isVisible = false
            similarMoviesAdapter.submitList(emptyList())
            return@with
        }

        val limitedMovies = movies.take(20)
        similarMoviesAdapter.submitList(limitedMovies)

        tvSimilaviesCount.text = when {
            movies.size > 20 -> "${movies.size} >"
            else -> movies.size.toString()
        }
        tvSimilaviesCount.isVisible = true
    }

    private fun formatYearAndGenres(movie: MovieDetailResponse): String {
        val year = movie.year?.takeIf { it.isNotBlank() }
        val genres = movie.genres
            .mapNotNull { it.genre.takeIf { genre -> genre.isNotBlank() } }
            .joinToString(", ")
            .takeIf { it.isNotBlank() }

        return listOfNotNull(year, genres).joinToString(", ")
    }

    private fun formatCountryDurationAge(movie: MovieDetailResponse): String {
        val countries = movie.countries
            .mapNotNull { it.country.takeIf { country -> country.isNotBlank() } }
            .joinToString(", ")
            .takeIf { it.isNotBlank() }
        val duration = movie.filmLength?.takeIf { it > 0 }?.let { formatDurationMinutes(it) }
        val age = movie.ratingAgeLimits
            ?.let { extractAgeLimit(it) }
            ?.takeIf { it.isNotBlank() }

        return listOfNotNull(countries, duration, age).joinToString(", ")
    }

    private fun extractAgeLimit(ageLimit: String): String {
        val digits = ageLimit.filter { it.isDigit() }
        return if (digits.isNotEmpty()) {
            "$digits+"
        } else {
            ageLimit
        }
    }

    private fun formatDurationMinutes(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "${hours} ч ${minutes} мин"
            hours > 0 -> "${hours} ч"
            else -> "${minutes} мин"
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        if (!isAdded) return

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        findNavController().navigate(R.id.action_movieDetailFragment_self, bundle)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}