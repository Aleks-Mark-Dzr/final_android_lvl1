package com.example.skillcinema.ui.actordetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentActorDetailBinding
//import com.example.skillcinema.domain.models.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.ui.adapters.FilmsAdapter
import com.example.skillcinema.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActorDetailFragment : Fragment() {

    private var _binding: FragmentActorDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ActorDetailFragmentArgs by navArgs()
    private val viewModel: ActorDetailViewModel by viewModels()

    private val filmsAdapter = FilmsAdapter { filmId ->
        navigateToFilmDetails(filmId)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentActorDetailBinding.inflate(inflater, container, false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupViews()
//        observeViewModel()
        viewModel.loadActorData(args.actorId)
    }

//    private fun setupViews() {
//        binding.apply {
//            filmsRecyclerView.apply {
//                layoutManager = GridLayoutManager(requireContext(), 2)
//                adapter = filmsAdapter
//            }
//
//            filmographyButton.setOnClickListener {
//                navigateToFilmography()
//            }
//
//            actorPhoto.setOnClickListener {
//                // Реализация полноэкранного просмотра фото
//            }
//        }
//    }

//    private fun observeViewModel() {
//        viewModel.actorDetails.observe(viewLifecycleOwner) { resource ->
//            when (resource) {
//                is Resource.Loading<*> -> showLoading()
//                is Resource.Success<*> -> showActorDetails(resource.data)
//                is Resource.Error<*> -> showError(resource.message)
//            }
//        }
//
//        viewModel.topFilms.observe(viewLifecycleOwner) { films ->
//            filmsAdapter.submitList(films.take(10))
//        }
//    }

//    private fun showLoading(): Any {
//
//    }

//    private fun showActorDetails(actor: Actor) {
//        binding.apply {
//            Glide.with(actorPhoto)
//                .load(actor.photoUrl)
//                .placeholder(R.drawable.ic_person_placeholder)
//                .error(R.drawable.ic_person_placeholder)
//                .into(actorPhoto)
//
//            actorName.text = actor.name
//            actorProfession.text = actor.profession
//
//            progressBar.visibility = View.GONE
//            contentContainer.visibility = View.VISIBLE
//        }
//    }

    private fun navigateToFilmDetails(filmId: Int) {
        findNavController().navigate(
//            R.id.action_actorDetailFragment_to_movieDetailFragment,
            Bundle().apply { putInt("filmId", filmId) }
        )
    }

    private fun navigateToFilmography() {
        findNavController().navigate(
            R.id.action_actorDetailFragment_to_filmographyFragment,
            Bundle().apply { putInt("actorId", args.actorId) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}