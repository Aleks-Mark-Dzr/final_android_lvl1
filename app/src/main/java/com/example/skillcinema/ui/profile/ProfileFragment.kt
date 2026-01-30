package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.skillcinema.data.Collection
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var watchedAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = requireActivity().application as SkillCinemaApp
        val factory = ProfileViewModelFactory(
            app.movieDetailRepository,
            app.profileRepository
        )
        viewModel = ViewModelProvider(requireActivity(), factory)[ProfileViewModel::class.java]
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

        collectionAdapter = CollectionAdapter(
            onClick = { collection ->
                navigateToCollection(collection.id)
            },
            onLongClick = { collection ->
                showCollectionActionsDialog(collection)
            }
        )

        historyAdapter = HistoryAdapter { item ->
            when (item.type) {
                ItemType.MOVIE -> navigateToMovie(item.id)
                ItemType.PERSON -> navigateToPerson(item.id)
                ItemType.SERIES -> navigateToSeries(item.id)
            }
        }

        watchedAdapter = MovieAdapter { movie ->
            navigateToMovie(movie.kinopoiskId)
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

        binding.rvViewed.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvViewed.adapter = watchedAdapter
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

                launch {
                    viewModel.watchedMovies.collect {
                        watchedAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.clearHistoryIcon.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.tvCreateYourOwnCollection.setOnClickListener {
            showCreateCollectionDialog()
        }
    }

    private fun showCreateCollectionDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.collection_name_hint)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_collection_title)
            .setView(input)
            .setPositiveButton(R.string.create_action) { _, _ ->
                val name = input.text.toString()
                if (name.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.collection_name_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.addCustomCollection(name)
                }
            }
            .setNegativeButton(R.string.cancel_action, null)
            .show()
    }

    private fun showCollectionActionsDialog(collection: Collection) {
        if (collection.id <= CUSTOM_COLLECTION_START_ID) return
        val options = arrayOf(
            getString(R.string.edit_collection_action),
            getString(R.string.delete_collection_action)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(collection.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditCollectionDialog(collection)
                    1 -> showDeleteCollectionDialog(collection)
                }
            }
            .show()
    }

    private fun showEditCollectionDialog(collection: Collection) {
        val input = EditText(requireContext()).apply {
            setText(collection.name)
            setSelection(collection.name.length)
            hint = getString(R.string.collection_name_hint)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_collection_title)
            .setView(input)
            .setPositiveButton(R.string.save_action) { _, _ ->
                val name = input.text.toString()
                if (name.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.collection_name_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.renameCustomCollection(collection.id, name)
                }
            }
            .setNegativeButton(R.string.cancel_action, null)
            .show()
    }

    private fun showDeleteCollectionDialog(collection: Collection) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_collection_title)
            .setMessage(R.string.delete_collection_message)
            .setPositiveButton(R.string.delete_collection_action) { _, _ ->
                viewModel.deleteCustomCollection(collection.id)
            }
            .setNegativeButton(R.string.cancel_action, null)
            .show()
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

    companion object {
        private const val CUSTOM_COLLECTION_START_ID = 2
    }
}