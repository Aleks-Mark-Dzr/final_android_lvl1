package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapters()
        setupRecyclerViews()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapters() {
        movieAdapter = MovieAdapter { movie ->
            navigateToMovie(movie.kinopoiskId)
        }

        collectionAdapter = CollectionAdapter { collection ->
            navigateToCollection(collection.id)
        }

        historyAdapter = HistoryAdapter { item ->
            when (item.type) {
                ItemType.MOVIE -> navigateToMovie(item.id)
                ItemType.PERSON -> navigateToPerson(item.id)
                ItemType.SERIES -> navigateToSeries(item.id)
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.rvActors.layoutManager = GridLayoutManager(context, 4)
        binding.rvActors.adapter = movieAdapter

        binding.rvCreateYourOwnCollection.layoutManager = GridLayoutManager(context, 2)
        binding.rvCreateYourOwnCollection?.let {
            it.adapter = collectionAdapter
        }


        binding.rvWereYouInterested.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWereYouInterested.adapter = historyAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viewed.collect {
                        movieAdapter.submitList(it)
                    }
                }

                launch {
                    viewModel.collections.collect {
                        collectionAdapter.submitList(it)
                    }
                }

                launch {
                    viewModel.history.collect {
                        historyAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.clearHistoryIcon.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun navigateToMovie(id: Int) {
//        val action = ProfileFragmentDirections.actionToMovieDetail(id)
//        findNavController().navigate(action)
    }

    private fun navigateToCollection(id: Int) {
        // Если нет навигации — просто лог или тост
        // val action = ProfileFragmentDirections.actionToCollectionDetail(id)
        // findNavController().navigate(action)
    }

    private fun navigateToPerson(id: Int) {
        // val action = ProfileFragmentDirections.actionToPersonDetail(id)
        // findNavController().navigate(action)
    }

    private fun navigateToSeries(id: Int) {
        // val action = ProfileFragmentDirections.actionToSeriesDetail(id)
        // findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}