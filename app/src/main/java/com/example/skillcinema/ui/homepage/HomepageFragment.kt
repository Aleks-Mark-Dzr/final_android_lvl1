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
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModel
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModelFactory
import com.example.skillcinema.utils.NetworkUtils
import kotlinx.coroutines.flow.collect
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
        val factory = HomepageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]

        premieresAdapter = CategoryMoviesAdapter("Премьеры") { movie ->
            // Обработчик клика
        }

        popularAdapter = CategoryMoviesAdapter("Популярное") { movie ->
            // Обработчик клика
        }

        dynamicCategoryAdapter = CategoryMoviesAdapter("Динамическая подборка") { movie ->
            // Обработчик клика
        }

        top250MoviesAdapter = CategoryMoviesAdapter("Топ-250") { movie ->
            // Обработчик клика
        }

        seriesAdapter = CategoryMoviesAdapter("Сериалы") { movie ->
            // Обработчик клика
        }

        // Настройка RecyclerView
        binding.rvPremieres.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPremieres.adapter = premieresAdapter

        binding.rvPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPopular.adapter = popularAdapter

        binding.rvDynamicCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDynamicCategory.adapter = dynamicCategoryAdapter

        binding.rvTop250Category.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTop250Category.adapter = top250MoviesAdapter

        binding.rvSeriesCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSeriesCategory.adapter = seriesAdapter

        // Подписка на StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.premieres.collect { moviesList ->
                    moviesList?.let { premieresAdapter.setData(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularMovies.collect { moviesList ->
                    moviesList?.let { popularAdapter.setData(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dynamicCategory.collect { moviesList ->
                    moviesList?.let { dynamicCategoryAdapter.setData(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.top250Movies.collect { moviesList ->
                    moviesList?.let {
                        top250MoviesAdapter.setData(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvSeries.collect { moviesList ->
                    moviesList?.let {
                        seriesAdapter.setData(it)
                    }
                }
            }
        }


        // Загрузка данных
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