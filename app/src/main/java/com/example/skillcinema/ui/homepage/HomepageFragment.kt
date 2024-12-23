package com.example.skillcinema.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.FragmentHomepageBinding
import com.example.skillcinema.network.MovieApiService
import com.example.skillcinema.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val movieApiService = RetrofitInstance.movieApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        val movieAdapter = MovieAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        // Загрузка данных
        lifecycleScope.launch {
            val movies = fetchMovies()
            movieAdapter.submitList(movies)
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun fetchMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.getTopMovies(page = 1)
                response.films
            } catch (e: Exception) {
                // Логирование ошибки и возврат пустого списка
                Log.e("HomepageFragment", "Error fetching movies: ${e.message}")
                emptyList()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}