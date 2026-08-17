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
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentFilmographyListBinding
import com.example.skillcinema.databinding.ItemFilmographyTabBinding
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.utils.Resource
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Фильмография актёра: группа табов «профессия + количество фильмов» и список фильмов
 * под каждым из них. Клик по фильму открывает экран фильма — вложенность не ограничена.
 */
class FilmographyListFragment : Fragment() {

    private var _binding: FragmentFilmographyListBinding? = null
    private val binding get() = _binding!!
    private val args: FilmographyListFragmentArgs by navArgs()
    private lateinit var viewModel: FilmographyViewModel

    private var tabMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupToolbar()
        observeViewModel()
        viewModel.loadFilmography(args.actorId)
    }

    private fun setupViewModel() {
        val repository = (requireActivity().application as SkillCinemaApp).actorRepository
        val factory = FilmographyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FilmographyViewModel::class.java]
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmography.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> showFilmography(resource.data)
                        is Resource.Error -> showError(resource.message)
                    }
                }
            }
        }
    }

    private fun showLoading() = with(binding) {
        progressBar.isVisible = true
        tabLayout.isVisible = false
        viewPager.isVisible = false
        errorMessage.isVisible = false
        emptyState.isVisible = false
    }

    private fun showError(message: String) = with(binding) {
        progressBar.isVisible = false
        tabLayout.isVisible = false
        viewPager.isVisible = false
        emptyState.isVisible = false
        errorMessage.isVisible = true
        errorMessage.text = message.ifBlank { getString(R.string.error_loading_films) }
    }

    private fun showFilmography(filmography: Map<Profession, List<Film>>) = with(binding) {
        progressBar.isVisible = false
        errorMessage.isVisible = false

        if (filmography.isEmpty()) {
            tabLayout.isVisible = false
            viewPager.isVisible = false
            emptyState.isVisible = true
            return@with
        }

        emptyState.isVisible = false
        tabLayout.isVisible = true
        viewPager.isVisible = true

        val professions = filmography.keys.toList()
        // Адаптер создаётся один раз — данные приходят одним ответом и дальше не меняются.
        if (viewPager.adapter == null) {
            viewPager.adapter = FilmographyPagerAdapter(this@FilmographyListFragment, professions)
            viewPager.offscreenPageLimit = 1

            tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                val profession = professions[position]
                val count = filmography[profession]?.size ?: 0
                val tabBinding = ItemFilmographyTabBinding.inflate(layoutInflater)
                tabBinding.tabTitle.text = profession.title
                tabBinding.tabCount.text = resources.getQuantityString(
                    R.plurals.films_count,
                    count,
                    count
                )
                tab.customView = tabBinding.root
            }.also { it.attach() }
        }
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
