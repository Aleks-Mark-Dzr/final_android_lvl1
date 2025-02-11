package com.example.skillcinema.ui.homepage

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentHomepageBinding
import com.example.skillcinema.domain.usecase.*
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModel
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModelFactory
import com.example.skillcinema.utils.NetworkUtils
import kotlinx.coroutines.launch

class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomepageViewModel
    private lateinit var premieresAdapter: CategoryMoviesAdapter
    private lateinit var popularAdapter: CategoryMoviesAdapter
    private lateinit var dynamicCategoryAdapter: CategoryMoviesAdapter
    private lateinit var top250MoviesAdapter: CategoryMoviesAdapter
    private lateinit var seriesAdapter: CategoryMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as SkillCinemaApp).movieRepository

        val factory = HomepageViewModelFactory(
            GetPremieresUseCase(repository),
            GetPopularMoviesUseCase(repository),
            GetMoviesByGenreAndCountryUseCase(repository),
            GetTop250MoviesUseCase(repository),
            GetTvSeriesUseCase(repository)
        )

        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]

        // Исправлено: передаем categoryTitle в адаптер
        premieresAdapter = CategoryMoviesAdapter(categoryTitle = "Премьеры") { movie -> }
        popularAdapter = CategoryMoviesAdapter(categoryTitle = "Популярное") { movie -> }
        dynamicCategoryAdapter = CategoryMoviesAdapter(categoryTitle = "Динамическая подборка") { movie -> }
        top250MoviesAdapter = CategoryMoviesAdapter(categoryTitle = "Топ-250") { movie -> }
        seriesAdapter = CategoryMoviesAdapter(categoryTitle = "Сериалы") { movie -> }

        binding.rvPremieres.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = premieresAdapter
        }
        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }
        binding.rvDynamicCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = dynamicCategoryAdapter
        }
        binding.rvTop250Category.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = top250MoviesAdapter
        }
        binding.rvSeriesCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = seriesAdapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.premieres.collect { moviesList -> premieresAdapter.setData(moviesList) }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularMovies.collect { moviesList -> popularAdapter.setData(moviesList) }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dynamicCategory.collect { moviesList -> dynamicCategoryAdapter.setData(moviesList) }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.top250Movies.collect { moviesList -> top250MoviesAdapter.setData(moviesList) }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvSeries.collect { moviesList -> seriesAdapter.setData(moviesList) }
            }
        }

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            viewModel.fetchPremieres(2025, "January")
            viewModel.fetchPopularMovies()
            viewModel.fetchDynamicCategory(countryId = 1, genreId = 2)
            viewModel.fetchTop250Movies(1)
            viewModel.fetchTvSeries(1)
        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding.tvAllPremieres.setOnClickListener { navigateToCategoryScreen("Премьеры") }
        binding.tvAllPopular.setOnClickListener { navigateToCategoryScreen("Популярное") }
        binding.tvAllDynamicCategory.setOnClickListener { navigateToCategoryScreen("Динамическая подборка") }
        binding.tvAllTop250Category.setOnClickListener { navigateToCategoryScreen("Топ-250") }
        binding.tvAllSeriesCategory.setOnClickListener { navigateToCategoryScreen("Сериалы") }
    }

    private fun navigateToCategoryScreen(category: String) {
        val bundle = Bundle().apply {
            putString("category", category)
        }
        findNavController().navigate(R.id.action_homepageFragment_to_moviesListFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}