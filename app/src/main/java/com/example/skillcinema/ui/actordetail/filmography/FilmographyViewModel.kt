// ui/actordetail/filmography/FilmographyViewModel.kt
package com.example.skillcinema.ui.actordetail.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.domain.repositories.ActorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/** Чип «профессия + количество фильмов». */
data class FilmographyChip(
    val profession: Profession,
    val title: String,
    val count: Int
)

data class FilmographyUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val actorName: String = "",
    val chips: List<FilmographyChip> = emptyList(),
    val selected: Profession? = null,
    val films: List<Film> = emptyList()
)

class FilmographyViewModel(
    private val repository: ActorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilmographyUiState())
    val uiState = _uiState.asStateFlow()

    private var filmography: Map<Profession, List<Film>> = emptyMap()

    /** Подгруженные карточки фильмов: id -> фильм с постером, годом и жанром. */
    private val cards = mutableMapOf<Int, Film>()
    private val requestedCards = mutableSetOf<Int>()

    // Фильмография может содержать сотни фильмов, поэтому карточки грузятся
    // только для показанных строк и не более нескольких запросов одновременно.
    private val cardLoadLimit = Semaphore(CONCURRENT_CARD_REQUESTS)

    private var loadedActorId: Int? = null

    fun load(actorId: Int) {
        if (loadedActorId == actorId) return
        loadedActorId = actorId

        viewModelScope.launch {
            _uiState.value = FilmographyUiState(isLoading = true)
            try {
                val actor = repository.getActorDetails(actorId)
                filmography = repository.getFilmography(actorId)

                val chips = filmography.map { (profession, films) ->
                    FilmographyChip(
                        profession = profession,
                        title = profession.titleFor(actor.isFemale),
                        count = films.size
                    )
                }

                _uiState.value = FilmographyUiState(
                    isLoading = false,
                    actorName = actor.name,
                    chips = chips,
                    selected = chips.firstOrNull()?.profession
                )
                publishFilms()
            } catch (e: Exception) {
                loadedActorId = null
                _uiState.value = FilmographyUiState(
                    isLoading = false,
                    errorMessage = e.message.orEmpty()
                )
            }
        }
    }

    fun selectProfession(profession: Profession) {
        if (_uiState.value.selected == profession) return
        _uiState.value = _uiState.value.copy(selected = profession)
        publishFilms()
    }

    /** Вызывается адаптером при показе строки — подгружает постер, год и жанр. */
    fun onFilmShown(filmId: Int) {
        if (!requestedCards.add(filmId)) return

        viewModelScope.launch {
            try {
                val card = cardLoadLimit.withPermit { repository.getFilmCard(filmId) }
                cards[filmId] = card
                publishFilms()
            } catch (e: Exception) {
                // Строка останется без постера — повторим попытку при следующем показе.
                requestedCards.remove(filmId)
            }
        }
    }

    private fun publishFilms() {
        val selected = _uiState.value.selected ?: return
        val films = filmography[selected].orEmpty().map { film ->
            val card = cards[film.id] ?: return@map film
            film.copy(
                title = card.title,
                year = card.year,
                posterUrl = card.posterUrl,
                rating = card.rating ?: film.rating,
                genre = card.genre
            )
        }
        _uiState.value = _uiState.value.copy(films = films)
    }

    private companion object {
        const val CONCURRENT_CARD_REQUESTS = 4
    }
}
