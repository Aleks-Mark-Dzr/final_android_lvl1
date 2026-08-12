package com.example.skillcinema.ui.search

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.SkillCinemaApp
import com.example.skillcinema.databinding.FragmentSearchSettingsBinding

class SearchSettingsFragment : Fragment() {

    private var _binding: FragmentSearchSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel

    /** Локальная копия настроек, применяется в общий ViewModel по мере изменений. */
    private lateinit var settings: SearchSettings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettingsBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as SkillCinemaApp).movieRepository
        val factory = SearchViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[SearchViewModel::class.java]

        settings = viewModel.settings.value

        bindType()
        bindSort()
        bindRating()
        bindFilters()
        bindNotViewed()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        return binding.root
    }

    // region Показывать (тип)
    private fun bindType() {
        binding.toggleType.check(
            when (settings.type) {
                SearchType.ALL -> R.id.btn_type_all
                SearchType.FILMS -> R.id.btn_type_films
                SearchType.TV_SERIES -> R.id.btn_type_series
            }
        )
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val type = when (checkedId) {
                R.id.btn_type_films -> SearchType.FILMS
                R.id.btn_type_series -> SearchType.TV_SERIES
                else -> SearchType.ALL
            }
            update { copy(type = type) }
        }
    }
    // endregion

    // region Сортировать
    private fun bindSort() {
        binding.toggleSort.check(
            when (settings.order) {
                SearchOrder.YEAR -> R.id.btn_sort_date
                SearchOrder.NUM_VOTE -> R.id.btn_sort_popularity
                SearchOrder.RATING -> R.id.btn_sort_rating
            }
        )
        binding.toggleSort.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val order = when (checkedId) {
                R.id.btn_sort_date -> SearchOrder.YEAR
                R.id.btn_sort_popularity -> SearchOrder.NUM_VOTE
                else -> SearchOrder.RATING
            }
            update { copy(order = order) }
        }
    }
    // endregion

    // region Рейтинг
    private fun bindRating() {
        val from = (settings.ratingFrom ?: 1).coerceIn(1, 10).toFloat()
        val to = (settings.ratingTo ?: 10).coerceIn(1, 10).toFloat()
        binding.sliderRating.values = listOf(minOf(from, to), maxOf(from, to))
        updateRatingLabel()

        binding.sliderRating.addOnChangeListener { slider, _, _ ->
            val f = slider.values[0].toInt()
            val t = slider.values[1].toInt()
            update { copy(ratingFrom = f, ratingTo = t) }
            updateRatingLabel()
        }
    }

    private fun updateRatingLabel() {
        val f = binding.sliderRating.values[0].toInt()
        val t = binding.sliderRating.values[1].toInt()
        binding.valueRating.text = if (f <= 1 && t >= 10) {
            getString(R.string.search_value_any)
        } else {
            getString(R.string.search_rating_range, f, t)
        }
    }
    // endregion

    // region Страна / Жанр / Год
    private fun bindFilters() {
        renderCountry()
        renderGenre()
        renderYear()

        binding.rowCountry.setOnClickListener { showCountryPicker() }
        binding.rowGenre.setOnClickListener { showGenrePicker() }
        binding.rowYear.setOnClickListener { showYearPicker() }
    }

    private fun renderCountry() {
        val name = viewModel.availableCountries.value.firstOrNull { it.id == settings.countryId }?.country
        binding.valueCountry.text = name ?: getString(R.string.search_value_any)
    }

    private fun renderGenre() {
        val name = viewModel.availableGenres.value.firstOrNull { it.id == settings.genreId }?.genre
        binding.valueGenre.text = name ?: getString(R.string.search_value_any)
    }

    private fun renderYear() {
        val from = settings.yearFrom ?: 1900
        val to = settings.yearTo ?: 2030
        binding.valueYear.text = getString(R.string.search_year_range, from, to)
    }

    private fun showCountryPicker() {
        val countries = viewModel.availableCountries.value
        if (countries.isEmpty()) return
        val labels = (listOf(getString(R.string.search_value_any)) + countries.map { it.country }).toTypedArray()
        val checked = countries.indexOfFirst { it.id == settings.countryId }.let { if (it >= 0) it + 1 else 0 }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.search_country)
            .setSingleChoiceItems(labels, checked) { dialog, which ->
                update { copy(countryId = if (which == 0) null else countries[which - 1].id) }
                renderCountry()
                dialog.dismiss()
            }
            .show()
    }

    private fun showGenrePicker() {
        val genres = viewModel.availableGenres.value
        if (genres.isEmpty()) return
        val labels = (listOf(getString(R.string.search_value_any)) + genres.map { it.genre }).toTypedArray()
        val checked = genres.indexOfFirst { it.id == settings.genreId }.let { if (it >= 0) it + 1 else 0 }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.search_genre)
            .setSingleChoiceItems(labels, checked) { dialog, which ->
                update { copy(genreId = if (which == 0) null else genres[which - 1].id) }
                renderGenre()
                dialog.dismiss()
            }
            .show()
    }

    private fun showYearPicker() {
        val ctx = requireContext()
        val pad = (16 * resources.displayMetrics.density).toInt()
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad + pad, pad, pad + pad, 0)
        }
        val inputFrom = EditText(ctx).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = getString(R.string.year_from)
            setText((settings.yearFrom ?: 1900).toString())
        }
        val inputTo = EditText(ctx).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = getString(R.string.year_to)
            setText((settings.yearTo ?: 2030).toString())
        }
        container.addView(inputFrom)
        container.addView(inputTo)

        AlertDialog.Builder(ctx)
            .setTitle(R.string.search_year)
            .setView(container)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val f = inputFrom.text.toString().toIntOrNull() ?: 1900
                val t = inputTo.text.toString().toIntOrNull() ?: 2030
                update { copy(yearFrom = minOf(f, t), yearTo = maxOf(f, t)) }
                renderYear()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    // endregion

    // region Не просмотрен
    private fun bindNotViewed() {
        renderNotViewed()
        binding.rowNotViewed.setOnClickListener {
            update { copy(hideViewed = !hideViewed) }
            renderNotViewed()
        }
    }

    private fun renderNotViewed() {
        val color = if (settings.hideViewed) R.color.search_accent else R.color.search_value
        val tint = ContextCompat.getColor(requireContext(), color)
        binding.iconNotViewed.setColorFilter(tint)
        binding.textNotViewed.setTextColor(tint)
    }
    // endregion

    /** Обновляет локальные настройки и сразу применяет их в общий ViewModel. */
    private inline fun update(transform: SearchSettings.() -> SearchSettings) {
        settings = settings.transform()
        viewModel.applySettings(settings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
