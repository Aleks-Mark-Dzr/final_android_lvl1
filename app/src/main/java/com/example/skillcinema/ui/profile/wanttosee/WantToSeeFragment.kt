package com.example.skillcinema.ui.profile.wanttosee

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
import com.example.skillcinema.databinding.FragmentWantToSeeBinding
import com.example.skillcinema.ui.search.SearchAdapter
import kotlinx.coroutines.launch

class WantToSeeFragment : Fragment() {

    private var _binding: FragmentWantToSeeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WantToSeeViewModel
    private lateinit var watchLaterAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWantToSeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = WantToSeeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WantToSeeViewModel::class.java]

        watchLaterAdapter = SearchAdapter { movieId ->
            navigateToMovieDetail(movieId)
        }

        binding.recyclerWantToSee.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = watchLaterAdapter
        }

        observeWatchLaterMovies()
    }

    private fun observeWatchLaterMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.watchLaterMovies.collect { movies ->
                    watchLaterAdapter.submitList(movies)
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

        if (findNavController().currentDestination?.id == R.id.wantToSeeFragment) {
            findNavController().navigate(
                R.id.action_wantToSeeFragment_to_movieDetailFragment,
                bundle,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}