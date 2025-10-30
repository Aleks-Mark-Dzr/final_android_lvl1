package com.example.skillcinema.ui.profile

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        if (id == 1) {
            findNavController().navigate(R.id.action_profileFragment_to_favoritesFragment)
        }
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