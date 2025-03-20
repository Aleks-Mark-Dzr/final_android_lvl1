package com.example.skillcinema.ui.search

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
import com.example.skillcinema.databinding.SearchFragmentBinding
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView
import com.example.skillcinema.SkillCinemaApp


class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)

        // Получаем экземпляр репозитория (если используете Dagger или Hilt, замените на инъекцию)
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository
        val factory = SearchViewModelFactory(repository)

        // Инициализируем ViewModel с фабрикой
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
//        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        setupUI()
        observeViewModel()
        return binding.root
    }

    private fun setupUI() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchMovies(newText.orEmpty())
                return true
            }
        })


        binding.buttonSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Открыть настройки поиска", Toast.LENGTH_SHORT).show()
        }

        searchAdapter = SearchAdapter { movie ->
            Toast.makeText(requireContext(), "Вы выбрали: ${movie.nameRu}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResults.adapter = searchAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collect { results ->
                    searchAdapter.submitList(results)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}