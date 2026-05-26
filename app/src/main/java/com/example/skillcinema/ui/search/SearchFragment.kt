package com.example.skillcinema.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.app.AlertDialog
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.databinding.SearchFragmentBinding
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView
import com.example.skillcinema.SkillCinemaApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.DialogSearchSettingsBinding
import com.google.android.material.slider.RangeSlider

class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter
    private var searchJob: Job? = null

    private val onMovieClick: (Int) -> Unit = { movieId ->
        Log.d("SearchFragment", "Клик по фильму с ID: $movieId")
        navigateToMovieDetail(movieId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)

        // Получаем экземпляр репозитория (если используете Dagger или Hilt, замените на инъекцию)
        val repository = (requireActivity().application as SkillCinemaApp).movieRepository
        val factory = SearchViewModelFactory(repository)

        // Инициализируем ViewModel с фабрикой
        viewModel = ViewModelProvider(requireActivity(), factory)[SearchViewModel::class.java]
        setupUI()
        observeViewModel()
        return binding.root
    }

    private fun setupUI() {
        val searchView = binding.searchView

        // Делаем строку поиска сразу активной и кликабельной

        searchView.isIconified = false
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }

        // Устанавливаем слушатель на ввод текста
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Не обрабатываем нажатие Enter
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel() // Отменяем предыдущий запрос

                searchJob = lifecycleScope.launch(Dispatchers.Main) {
                    delay(250) // Небольшая задержка, чтобы обновлять поиск по каждой введенной букве/цифре
                    val query = newText.orEmpty()
                    Log.d("SearchFragment", "Отправка запроса: $query")
                    viewModel.searchMovies(query)
                }
                return true
            }
        })

        binding.buttonSettings.setOnClickListener { showSearchSettingsDialog() }

        searchAdapter = SearchAdapter(onMovieClick)

        binding.recyclerSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResults.adapter = searchAdapter
    }

    private fun showSearchSettingsDialog() {
        val dialogBinding = DialogSearchSettingsBinding.inflate(layoutInflater)
        val currentSettings = viewModel.settings.value
        val countries = viewModel.availableCountries.value
        val genres = viewModel.availableGenres.value

        dialogBinding.radioGroupType.check(
            when (currentSettings.type) {
                SearchType.ALL -> R.id.radio_all
                SearchType.FILMS -> R.id.radio_films
                SearchType.TV_SERIES -> R.id.radio_series
            }
        )

        val countryNames = listOf(getString(R.string.not_selected)) + countries.map { it.country }
        dialogBinding.spinnerCountry.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countryNames)
        val countryIndex = countries.indexOfFirst { it.id == currentSettings.countryId }
        dialogBinding.spinnerCountry.setSelection(if (countryIndex >= 0) countryIndex + 1 else 0)

        val genreNames = listOf(getString(R.string.not_selected)) + genres.map { it.genre }
        dialogBinding.spinnerGenre.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genreNames)
        val genreIndex = genres.indexOfFirst { it.id == currentSettings.genreId }
        dialogBinding.spinnerGenre.setSelection(if (genreIndex >= 0) genreIndex + 1 else 0)

        dialogBinding.editYearFrom.setText((currentSettings.yearFrom ?: 1900).toString())
        dialogBinding.editYearTo.setText((currentSettings.yearTo ?: 2030).toString())

        dialogBinding.sliderRating.values = listOf(
            (currentSettings.ratingFrom ?: 1).toFloat(),
            (currentSettings.ratingTo ?: 10).toFloat()
        )
        updateRatingLabel(dialogBinding.sliderRating, dialogBinding)
        dialogBinding.sliderRating.addOnChangeListener { slider, _, _ -> updateRatingLabel(slider, dialogBinding) }

        val orderItems = listOf(
            getString(R.string.order_rating),
            getString(R.string.order_popularity),
            getString(R.string.order_date)
        )
        dialogBinding.spinnerOrder.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, orderItems)
        dialogBinding.spinnerOrder.setSelection(currentSettings.order.ordinal)
        dialogBinding.switchHideViewed.isChecked = currentSettings.hideViewed

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
            .apply {
                dialogBinding.buttonApply.setOnClickListener {
                    val selectedType = when (dialogBinding.radioGroupType.checkedRadioButtonId) {
                        R.id.radio_films -> SearchType.FILMS
                        R.id.radio_series -> SearchType.TV_SERIES
                        else -> SearchType.ALL
                    }
                    viewModel.applySettings(
                        SearchSettings(
                            type = selectedType,
                            countryId = countries.getOrNull(dialogBinding.spinnerCountry.selectedItemPosition - 1)?.id,
                            genreId = genres.getOrNull(dialogBinding.spinnerGenre.selectedItemPosition - 1)?.id,
                            yearFrom = dialogBinding.editYearFrom.text.toString().toIntOrNull(),
                            yearTo = dialogBinding.editYearTo.text.toString().toIntOrNull(),
                            ratingFrom = dialogBinding.sliderRating.values[0].toInt(),
                            ratingTo = dialogBinding.sliderRating.values[1].toInt(),
                            order = SearchOrder.entries[dialogBinding.spinnerOrder.selectedItemPosition],
                            hideViewed = dialogBinding.switchHideViewed.isChecked
                        )
                    )
                    dismiss()
                }
                show()
            }
    }

    private fun updateRatingLabel(slider: RangeSlider, binding: DialogSearchSettingsBinding) {
        binding.textRatingValue.text = getString(
            R.string.rating_range_value,
            slider.values[0].toInt(),
            slider.values[1].toInt()
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collect { results ->
                    Log.d("SearchFragment", "Обновление UI, найдено: ${results.size} фильмов")
                    searchAdapter.submitList(results)
                }
            }
        }
    }

    // ✅ переход на `MovieDetailFragment`
    private fun navigateToMovieDetail(movieId: Int) {
        if (!isAdded) return // Проверяем, что фрагмент еще добавлен в NavController

        Log.d("SearchFragment", "Навигация в MovieDetailFragment с movieId = $movieId")

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }

        findNavController().navigate(R.id.action_searchFragment_to_movieDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}