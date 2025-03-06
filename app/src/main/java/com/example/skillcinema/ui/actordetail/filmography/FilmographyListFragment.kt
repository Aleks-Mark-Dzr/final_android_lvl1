package com.example.skillcinema.ui.actordetail.filmography

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.databinding.FragmentFilmographyListBinding
import com.example.skillcinema.domain.models.Film
//import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.ui.adapters.FilmsAdapter
import com.example.skillcinema.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmographyListFragment : Fragment() {

    private var _binding: FragmentFilmographyListBinding? = null
    private val binding get() = _binding!!
    private val args: FilmographyListFragmentArgs by navArgs()
    private val viewModel: FilmographyViewModel by viewModels()

    private val filmsAdapter = FilmsAdapter(::onFilmClicked)

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupRecyclerView()
//        observeData()
        loadData()
    }

//    private fun setupRecyclerView() {
//        binding.filmsRecyclerView.apply {
//            layoutManager = GridLayoutManager(requireContext(), 2)
//            adapter = filmsAdapter
//            setHasFixedSize(true)
//        }
//    }

    private fun loadData() {
        viewModel.loadFilmography(
            actorId = args.actorId,
            profession = args.profession
        )
    }

//    private fun observeData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.films.collectLatest { resource ->
//                    when (resource) {
//                        is Resource.Loading -> showLoading()
//                        is Resource.Success -> showFilms(resource.data)
//                        is Resource.Error -> showError(resource.message)
//                    }
//                }
//            }
//        }
//    }

//    private fun showLoading() {
//        binding.apply {
//            progressBar.visibility = View.VISIBLE
//            filmsRecyclerView.visibility = View.GONE
//            errorMessage.visibility = View.GONE
//        }
//    }

//    private fun showFilms(films: List<Film>) {
//        binding.apply {
//            progressBar.visibility = View.GONE
//            filmsRecyclerView.visibility = View.VISIBLE
//            errorMessage.visibility = View.GONE
//
//            filmsAdapter.submitList(films)
//            binding.emptyState.visibility = if (films.isEmpty()) View.VISIBLE else View.GONE
//        }
//    }

//    private fun showError(message: String?) {
//        binding.apply {
//            progressBar.visibility = View.GONE
//            filmsRecyclerView.visibility = View.GONE
//            errorMessage.visibility = View.VISIBLE
//            errorMessage.text = message ?: getString(R.string.error_loading_films)
//        }
//    }

    private fun onFilmClicked(filmId: Int) {
        // Навигация к деталям фильма
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    companion object {
//        fun newInstance(actorId: Int, profession: Profession, count: Int) =
//            FilmographyListFragment().apply {
//                arguments = Bundle().apply {
//                    putInt("actorId", actorId)
//                    putSerializable("profession", profession)
//                    putInt("count", count)
//                }
//            }
//    }

    private fun setupViews() {
        // Пример доступа к элементам
//        binding.filmsRecyclerView.adapter = ... // Настройка RecyclerView
        binding.progressBar.visibility = View.VISIBLE // Показать ProgressBar
        binding.errorMessage.text = "Ошибка загрузки" // Установить текст ошибки
        binding.emptyState.text = "Нет данных" // Установить текст пустого состояния
    }
}