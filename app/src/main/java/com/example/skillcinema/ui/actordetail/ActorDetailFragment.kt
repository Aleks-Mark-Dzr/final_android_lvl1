package com.example.skillcinema.ui.actordetail

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.data.Actor
import com.example.skillcinema.databinding.FragmentActorDetailBinding
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.ui.adapters.FilmsAdapter
import com.example.skillcinema.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActorDetailFragment : Fragment() {

    private var _binding: FragmentActorDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ActorDetailFragmentArgs by navArgs()
    private lateinit var viewModel: ActorDetailViewModel

    private val filmsAdapter = FilmsAdapter { filmId ->
        navigateToFilmDetails(filmId)
    }

    private var actorPhotoUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupViews()
        observeViewModel()
        viewModel.loadActorData(args.actorId)
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).actorRepository
        val factory = ActorDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ActorDetailViewModel::class.java]
    }

    private fun setupViews() {
        binding.apply {
            filmsRecyclerView.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = filmsAdapter
            }

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            filmographyButton.setOnClickListener {
                navigateToFilmography()
            }

            actorPhoto.setOnClickListener {
                actorPhotoUrl?.let { url -> showFullScreenPhoto(url) }
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actorDetails.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> showActorDetails(resource.data)
                        is Resource.Error -> showError(resource.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topFilms.collectLatest { films ->
                    renderTopFilms(films)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmsCount.collectLatest { count ->
                    renderFilmographyCount(count)
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            errorMessage.isVisible = false
            contentContainer.isVisible = false
        }
    }

    private fun showActorDetails(actor: Actor) {
        binding.apply {
            Glide.with(actorPhoto)
                .load(actor.photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(actorPhoto)

            actorName.text = actor.name
            actorProfession.text = actor.profession.orEmpty()
            actorProfession.isVisible = !actor.profession.isNullOrBlank()
            actorPhotoUrl = actor.photoUrl

            progressBar.isVisible = false
            errorMessage.isVisible = false
            contentContainer.isVisible = true
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.isVisible = false
            errorMessage.isVisible = true
            errorMessage.text = message.ifBlank {
                getString(R.string.error_loading_actor)
            }
            contentContainer.isVisible = false
        }
    }

    private fun renderTopFilms(films: List<Film>) {
        val hasFilms = films.isNotEmpty()
        filmsAdapter.submitList(films)
        binding.topFilmsTitle.isVisible = hasFilms
        binding.filmsRecyclerView.isVisible = hasFilms
    }

    private fun renderFilmographyCount(count: Int) {
        binding.filmographyButton.isVisible = count > 0
        binding.filmographyCount.text = resources.getQuantityString(
            R.plurals.films_count,
            count,
            count
        )
    }

    private fun navigateToFilmDetails(filmId: Int) {
        if (!isAdded) return
        val bundle = Bundle().apply { putInt("movieId", filmId) }
        findNavController().navigate(R.id.movieDetailFragment, bundle)
    }

    private fun navigateToFilmography() {
        if (!isAdded) return
        findNavController().navigate(
            R.id.action_actorDetailFragment_to_filmographyFragment,
            Bundle().apply { putInt("actorId", args.actorId) }
        )
    }

    private fun showFullScreenPhoto(url: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = ImageView(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = getString(R.string.actor_photo)
        }
        Glide.with(imageView)
            .load(url)
            .placeholder(R.drawable.ic_person_placeholder)
            .error(R.drawable.ic_person_placeholder)
            .into(imageView)
        dialog.setContentView(imageView)
        imageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        binding.filmsRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
