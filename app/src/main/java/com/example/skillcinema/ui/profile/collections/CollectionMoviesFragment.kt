package com.example.skillcinema.ui.profile.collections

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentCollectionMoviesBinding
import com.example.skillcinema.ui.search.SearchAdapter
import kotlinx.coroutines.launch

class CollectionMoviesFragment : Fragment() {

    private var _binding: FragmentCollectionMoviesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CollectionMoviesViewModel
    private lateinit var moviesAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCollectionMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectionId = requireArguments().getInt(ARG_COLLECTION_ID)
        val collectionName = requireArguments().getString(ARG_COLLECTION_NAME).orEmpty()
        binding.tvCollectionTitle.text = collectionName

        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = CollectionMoviesViewModelFactory(repository, collectionId)
        viewModel = ViewModelProvider(this, factory)[CollectionMoviesViewModel::class.java]

        moviesAdapter = SearchAdapter { movieId ->
            navigateToMovieDetail(movieId)
        }

        binding.recyclerCollectionMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moviesAdapter
        }

        observeMovies()
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collect { movies ->
                    moviesAdapter.submitList(movies)
                    binding.emptyView.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        if (!isAdded) return

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        if (findNavController().currentDestination?.id == R.id.collectionMoviesFragment) {
            findNavController().navigate(
                R.id.action_collectionMoviesFragment_to_movieDetailFragment,
                bundle,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_COLLECTION_ID = "collectionId"
        const val ARG_COLLECTION_NAME = "collectionName"
    }
}