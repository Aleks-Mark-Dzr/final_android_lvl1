package com.example.skillcinema.ui.moviedetail.crew

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
import com.example.skillcinema.databinding.FragmentCrewListBinding
import com.example.skillcinema.ui.adapters.CrewAdapter
import com.example.skillcinema.ui.moviedetail.viewmodel.CrewListViewModel
import com.example.skillcinema.ui.moviedetail.viewmodel.CrewListViewModelFactory
import kotlinx.coroutines.launch

class CrewListFragment : Fragment() {

    private var _binding: FragmentCrewListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrewListViewModel
    private val crewAdapter by lazy {
        CrewAdapter { _ ->
            // TODO: переход на экран персона
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCrewListBinding.inflate(inflater, container, false)
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

        viewModel.loadCrew(movieId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerCrew.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = crewAdapter
        }
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = CrewListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CrewListViewModel::class.java]
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crew.collect { crew ->
                    crewAdapter.submitList(crew)
                    binding.recyclerCrew.isVisible = crew.isNotEmpty()
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
        val hasCrew = viewModel.crew.value.isNotEmpty()
        val isLoading = viewModel.isLoading.value
        val errorMessage = viewModel.errorMessage.value

        when {
            isLoading -> {
                binding.emptyView.isVisible = false
            }

            !errorMessage.isNullOrBlank() -> {
                binding.emptyView.isVisible = true
                binding.emptyView.text = errorMessage
                binding.recyclerCrew.isVisible = false
            }

            !hasCrew -> {
                binding.emptyView.isVisible = true
                binding.emptyView.text = getString(R.string.no_crew_available)
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