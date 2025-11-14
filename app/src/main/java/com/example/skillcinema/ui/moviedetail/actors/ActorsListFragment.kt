package com.example.skillcinema.ui.moviedetail.actors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentActorsListBinding
import com.example.skillcinema.ui.adapters.ActorsAdapter
import com.example.skillcinema.ui.moviedetail.viewmodel.ActorsListViewModel
import com.example.skillcinema.ui.moviedetail.viewmodel.ActorsListViewModelFactory
import kotlinx.coroutines.launch

class ActorsListFragment : Fragment() {

    private var _binding: FragmentActorsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ActorsListViewModel
    private val actorsAdapter by lazy {
        ActorsAdapter { _ ->
            // TODO: переход на экран актера
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentActorsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getInt(MOVIE_ID_KEY) ?: 0
        if (movieId == 0) {
            binding.progressBar.isVisible = false
            binding.emptyView.apply {
                isVisible = true
                text = getString(R.string.error_movie_not_found)
            }
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        observeViewModel()

        viewModel.loadActors(movieId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerActors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actorsAdapter
        }
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = ActorsListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ActorsListViewModel::class.java]
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actors.collect { actors ->
                    actorsAdapter.submitList(actors)
                    binding.recyclerActors.isVisible = actors.isNotEmpty()
                    updateEmptyState()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.isVisible = isLoading
                    updateEmptyState()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    updateEmptyState()
                }
            }
        }
    }

    private fun updateEmptyState() {
        val hasActors = viewModel.actors.value.isNotEmpty()
        val isLoading = viewModel.isLoading.value
        val errorMessage = viewModel.errorMessage.value

        when {
            isLoading -> {
                binding.emptyView.isVisible = false
            }

            !errorMessage.isNullOrBlank() -> {
                binding.emptyView.isVisible = true
                binding.emptyView.text = errorMessage
                binding.recyclerActors.isVisible = false
            }

            !hasActors -> {
                binding.emptyView.isVisible = true
                binding.emptyView.text = getString(R.string.no_actors_available)
            }

            else -> {
                binding.emptyView.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MOVIE_ID_KEY = "movieId"
    }
}