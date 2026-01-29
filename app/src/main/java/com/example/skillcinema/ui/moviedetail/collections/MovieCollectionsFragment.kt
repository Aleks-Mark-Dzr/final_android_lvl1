package com.example.skillcinema.ui.moviedetail.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.GlideApp
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.databinding.FragmentMovieCollectionsBinding
import com.example.skillcinema.ui.profile.ProfileViewModel
import com.example.skillcinema.ui.profile.ProfileViewModelFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MovieCollectionsFragment : Fragment() {

    private var _binding: FragmentMovieCollectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieCollectionsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var adapter: MovieCollectionsAdapter
    private var movieId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("movieId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupRecyclerView()
        setupActions()
        observeUi()
    }

    private fun setupViewModels() {
        val repository = (requireActivity().application as SkillCinemaApp).movieDetailRepository
        val factory = MovieCollectionsViewModelFactory(repository, movieId)
        viewModel = ViewModelProvider(this, factory)[MovieCollectionsViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(repository)
        profileViewModel = ViewModelProvider(requireActivity(), profileFactory)[ProfileViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = MovieCollectionsAdapter { item, isChecked ->
            viewModel.toggleCollection(item.id, isChecked)
        }
        binding.rvMovieCollections.adapter = adapter
    }

    private fun setupActions() {
        binding.btnCreateCollection.setOnClickListener {
            showCreateCollectionDialog()
        }
    }

    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    profileViewModel.collections,
                    viewModel.uiState
                ) { collections, state -> collections to state }
                    .collect { (collections, state) ->
                        val items = collections.map { collection ->
                            MovieCollectionItemUi(
                                id = collection.id,
                                name = collection.name,
                                count = state.collectionCounts[collection.id] ?: 0,
                                isChecked = state.selectedCollections.contains(collection.id)
                            )
                        }
                        adapter.submitList(items)
                        renderMovie(state.movie)
                    }
            }
        }
    }

    private fun renderMovie(movie: MovieDetailResponse?) {
        if (movie == null) {
            binding.tvCollectionMovieTitle.isVisible = false
            binding.tvCollectionMovieRating.isVisible = false
            binding.tvCollectionMovieGenres.isVisible = false
            return
        }
        binding.tvCollectionMovieTitle.isVisible = true
        binding.tvCollectionMovieRating.isVisible = true
        binding.tvCollectionMovieGenres.isVisible = true
        binding.tvCollectionMovieTitle.text = movie.nameRu ?: getString(R.string.app_name)
        binding.tvCollectionMovieRating.text = movie.ratingKinopoisk?.toString() ?: "N/A"
        binding.tvCollectionMovieGenres.text = movie.genres.joinToString(", ") { it.genre }
        GlideApp.with(this)
            .load(movie.posterUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivCollectionPoster)
    }

    private fun showCreateCollectionDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.collection_name_hint)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_collection_title)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.collection_name_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val newId = profileViewModel.addCustomCollection(name)
                newId?.let { viewModel.selectCollection(it) }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}