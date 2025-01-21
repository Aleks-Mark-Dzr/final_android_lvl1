package com.example.skillcinema.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentHomepageBinding
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModel
import com.example.skillcinema.ui.homepage.viewmodel.HomepageViewModelFactory
import com.example.skillcinema.utils.NetworkUtils
import kotlinx.coroutines.flow.collect

class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val movieAdapter = MovieAdapter()

    private lateinit var viewModel: HomepageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация ViewModel
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository
        val factory = HomepageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomepageViewModel::class.java)

        // Настройка RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        // Подписка на StateFlow из ViewModel
        lifecycleScope.launchWhenStarted {
            viewModel.movies.collect { movies ->
                movieAdapter.submitList(movies)
            }
        }

        // Проверка соединения перед загрузкой фильмов
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            viewModel.fetchMovies()
        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}