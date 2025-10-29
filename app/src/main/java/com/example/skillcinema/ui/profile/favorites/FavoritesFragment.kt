package com.example.skillcinema.ui.profile.favorites

import android.os.Bundle
import android.util.Log
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
import com.example.skillcinema.databinding.FragmentFavoritesBinding
import com.example.skillcinema.ui.search.SearchAdapter
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var favoritesAdapter: SearchAdapter

    private val onMovieClick: (Int) -> Unit = { movieId ->
        Log.d("FavoritesFragment", "Click on favorite movie: $movieId")
        navigateToMovieDetail(movieId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = FavoritesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]

        favoritesAdapter = SearchAdapter(onMovieClick)

        binding.recyclerFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter
        }

        observeFavorites()
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteMovies.collect { favorites ->
                    favoritesAdapter.submitList(favorites)
                    binding.emptyView.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        if (!isAdded) return

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        if (findNavController().currentDestination?.id == R.id.favoritesFragment) {
            findNavController().navigate(
                R.id.action_favoritesFragment_to_movieDetailFragment,
                bundle,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}