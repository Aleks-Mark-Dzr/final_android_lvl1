package com.example.skillcinema.ui.actordetail.filmography

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentFilmographyListBinding
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.ui.adapters.FilmographyAdapter
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Фильмография актёра: группа чипов «профессия + количество фильмов» и список фильмов
 * выбранной профессии. Клик по фильму открывает экран фильма — вложенность не ограничена.
 */
class FilmographyListFragment : Fragment() {

    private var _binding: FragmentFilmographyListBinding? = null
    private val binding get() = _binding!!
    private val args: FilmographyListFragmentArgs by navArgs()
    private lateinit var viewModel: FilmographyViewModel

    private val filmsAdapter by lazy {
        FilmographyAdapter(
            onFilmShown = { filmId -> viewModel.onFilmShown(filmId) },
            onFilmClick = { filmId -> navigateToFilmDetails(filmId) }
        )
    }

    /** Чипы создаются один раз — держим их, чтобы обновлять подсветку счётчика. */
    private val chipsByProfession = mutableMapOf<Profession, Chip>()
    private var renderedSelection: Profession? = null

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
        setupRecyclerView()
        observeViewModel()
        viewModel.load(args.actorId)
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

    private fun setupRecyclerView() {
        binding.filmsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filmsAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { render(it) }
            }
        }
    }

    private fun render(state: FilmographyUiState) = with(binding) {
        progressBar.isVisible = state.isLoading

        val error = state.errorMessage
        if (!state.isLoading && error != null) {
            errorMessage.isVisible = true
            errorMessage.text = error.ifBlank { getString(R.string.error_loading_films) }
        } else {
            errorMessage.isVisible = false
        }

        val hasContent = !state.isLoading && error == null && state.chips.isNotEmpty()
        actorName.isVisible = hasContent
        chipsScrollContainer.isVisible = hasContent
        filmsRecyclerView.isVisible = hasContent
        emptyState.isVisible = !state.isLoading && error == null && state.chips.isEmpty()

        if (!hasContent) return@with

        actorName.text = state.actorName
        renderChips(state)
        filmsAdapter.submitList(state.films)
    }

    private fun renderChips(state: FilmographyUiState) {
        val chipGroup = binding.chipGroupProfessions
        val isFirstRender = chipsByProfession.isEmpty()

        // Состояние обновляется на каждую подгруженную карточку фильма,
        // поэтому чипы перерисовываем только когда действительно сменился выбор.
        if (!isFirstRender && renderedSelection == state.selected) return
        renderedSelection = state.selected

        if (isFirstRender) {
            state.chips.forEach { item ->
                val chip = layoutInflater.inflate(
                    R.layout.item_filmography_chip,
                    chipGroup,
                    false
                ) as Chip
                chip.setOnClickListener { viewModel.selectProfession(item.profession) }
                chipGroup.addView(chip)
                chipsByProfession[item.profession] = chip
            }
        }

        state.chips.forEach { item ->
            val chip = chipsByProfession[item.profession] ?: return@forEach
            val isSelected = item.profession == state.selected
            chip.isChecked = isSelected
            chip.text = chipLabel(item.title, item.count, isSelected)
        }
    }

    /** «Актриса 33»: счётчик мельче и приглушённее основного текста. */
    private fun chipLabel(title: String, count: Int, isSelected: Boolean): CharSequence {
        val countColor = if (isSelected) {
            Color.argb(SELECTED_COUNT_ALPHA, 255, 255, 255)
        } else {
            ContextCompat.getColor(requireContext(), R.color.search_label)
        }

        return SpannableStringBuilder(title).apply {
            append("  ")
            val start = length
            append(count.toString())
            setSpan(RelativeSizeSpan(0.85f), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(countColor), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun navigateToFilmDetails(filmId: Int) {
        if (!isAdded) return
        val bundle = Bundle().apply { putInt("movieId", filmId) }
        findNavController().navigate(R.id.movieDetailFragment, bundle)
    }

    override fun onDestroyView() {
        binding.filmsRecyclerView.adapter = null
        chipsByProfession.clear()
        renderedSelection = null
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        /** Счётчик на выбранном чипе — приглушённый белый поверх акцентной заливки. */
        const val SELECTED_COUNT_ALPHA = 170
    }
}
