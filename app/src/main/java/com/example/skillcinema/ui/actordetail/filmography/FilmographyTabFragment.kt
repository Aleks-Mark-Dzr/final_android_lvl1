package com.example.skillcinema.ui.actordetail.filmography

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentFilmographyTabBinding
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.ui.adapters.FilmographyAdapter
import com.example.skillcinema.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Список фильмов одной профессии внутри [FilmographyListFragment]. */
class FilmographyTabFragment : Fragment() {

    private var _binding: FragmentFilmographyTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FilmographyViewModel

    private val profession: Profession by lazy {
        Profession.fromKey(arguments?.getString(ARG_PROFESSION_KEY))
    }

    private val filmsAdapter = FilmographyAdapter { filmId ->
        navigateToFilmDetails(filmId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).actorRepository
        val factory = FilmographyViewModelFactory(repository)
        // Фильмография уже загружена родительским экраном — переиспользуем его ViewModel.
        viewModel = ViewModelProvider(
            requireParentFragment(),
            factory
        )[FilmographyViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.filmsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filmsAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmography.collectLatest { resource ->
                    val films = (resource as? Resource.Success)?.data?.get(profession).orEmpty()
                    filmsAdapter.submitList(films)
                    binding.emptyState.isVisible = films.isEmpty()
                    binding.filmsRecyclerView.isVisible = films.isNotEmpty()
                }
            }
        }
    }

    private fun navigateToFilmDetails(filmId: Int) {
        if (!isAdded) return
        val bundle = Bundle().apply { putInt("movieId", filmId) }
        findNavController().navigate(R.id.movieDetailFragment, bundle)
    }

    override fun onDestroyView() {
        binding.filmsRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PROFESSION_KEY = "professionKey"

        fun newInstance(profession: Profession) = FilmographyTabFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PROFESSION_KEY, profession.key)
            }
        }
    }
}
