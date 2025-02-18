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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentMoviesListBinding
import com.example.skillcinema.domain.usecase.*
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

        // ✅ Проверяем, существует ли category, если нет — используем дефолтное значение
        binding.tvCategoryTitle.text = category ?: "Фильмы"

        // ✅ Получаем репозиторий из Application
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository

        // ✅ Создаем ViewModel через Factory
        val factory = MoviesListViewModelFactory(
            GetPremieresUseCase(repository),
            GetPopularMoviesUseCase(repository),
            GetMoviesByGenreAndCountryUseCase(repository),
            GetTop250MoviesUseCase(repository),
            GetTvSeriesUseCase(repository)
        )

        viewModel = ViewModelProvider(this, factory)[MoviesListViewModel::class.java]

        // ✅ Исправлено: `onMovieClick` теперь объявлен в `MoviesListFragment`, а не внутри `onViewCreated`
        adapter = CategoryMoviesAdapter { movieId ->
            navigateToMovieDetail(movieId)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MoviesListFragment.adapter
        }

        // ✅ Загружаем фильмы
        viewModel.loadMoviesForCategory(category ?: "")

        // ✅ Подписка на `StateFlow` (заменен `setData` на `submitList`)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { movies ->
                    adapter.submitList(movies)
                }
            }
        }
    }

    // ✅ Исправленный метод перехода в `MovieDetailFragment`
    private fun navigateToMovieDetail(movieId: Int) {
        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        // ✅ Проверяем, что `NavController` доступен, прежде чем вызывать навигацию
        if (findNavController().currentDestination?.id == R.id.moviesListFragment) {
            findNavController().navigate(R.id.action_moviesListFragment_to_movieDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}