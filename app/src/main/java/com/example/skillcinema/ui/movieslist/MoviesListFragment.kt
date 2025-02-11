package com.example.skillcinema.ui.movieslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentMoviesListBinding
import com.example.skillcinema.domain.usecase.GetMoviesByGenreAndCountryUseCase
import com.example.skillcinema.domain.usecase.GetPopularMoviesUseCase
import com.example.skillcinema.domain.usecase.GetPremieresUseCase
import com.example.skillcinema.domain.usecase.GetTop250MoviesUseCase
import com.example.skillcinema.domain.usecase.GetTvSeriesUseCase
import com.example.skillcinema.ui.homepage.CategoryMoviesAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MoviesListFragment : Fragment() {

    private var _binding: FragmentMoviesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MoviesListViewModel
    private lateinit var adapter: CategoryMoviesAdapter
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем репозиторий из приложения
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository

        // Создаем UseCase-классы
        val factory = MoviesListViewModelFactory(
            GetPremieresUseCase(repository),
            GetPopularMoviesUseCase(repository),
            GetMoviesByGenreAndCountryUseCase(repository),
            GetTop250MoviesUseCase(repository),
            GetTvSeriesUseCase(repository)
        )

        // Создаем ViewModel через Factory
        viewModel = ViewModelProvider(this, factory)[MoviesListViewModel::class.java]

        // Исправлено: передаем правильный параметр в адаптер
        adapter = CategoryMoviesAdapter(categoryTitle = category ?: "", isGrid = true) { movie ->
            // Обработчик клика по фильму
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MoviesListFragment.adapter
        }

        // Загружаем фильмы
        viewModel.loadMoviesForCategory(category ?: "")

        // Подписка на StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { movies ->
                    adapter.setData(movies)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}