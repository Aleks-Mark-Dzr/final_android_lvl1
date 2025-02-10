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
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Получаем репозиторий из приложения
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository

        // Создаем UseCase-классы
        val factory = HomepageViewModelFactory(
            GetPremieresUseCase(repository),
            GetPopularMoviesUseCase(repository),
            GetMoviesByGenreAndCountryUseCase(repository),
            GetTop250MoviesUseCase(repository),
            GetTvSeriesUseCase(repository)
        )

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]

        // Настройка адаптеров
        premieresAdapter = CategoryMoviesAdapter("Премьеры") { movie -> /* Обработчик клика */ }
        popularAdapter = CategoryMoviesAdapter("Популярное") { movie -> /* Обработчик клика */ }
        dynamicCategoryAdapter = CategoryMoviesAdapter("Динамическая подборка") { movie -> /* Обработчик клика */ }
        top250MoviesAdapter = CategoryMoviesAdapter("Топ-250") { movie -> /* Обработчик клика */ }
        seriesAdapter = CategoryMoviesAdapter("Сериалы") { movie -> /* Обработчик клика */ }

        // Настройка RecyclerView
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

        // Подписка на StateFlow
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

        // Проверка интернета и загрузка данных
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            viewModel.fetchPremieres(2025, "January")
            viewModel.fetchPopularMovies()
            viewModel.fetchDynamicCategory(countryId = 1, genreId = 2) // Пример
            viewModel.fetchTop250Movies(1)
            viewModel.fetchTvSeries(1)
        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}