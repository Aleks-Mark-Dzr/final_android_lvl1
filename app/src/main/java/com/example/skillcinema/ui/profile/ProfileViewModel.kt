package com.example.skillcinema.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.HistoryItem
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.skillcinema.data.Collection


class ProfileViewModel : ViewModel() {

    private val _viewed = MutableStateFlow<List<Movie>>(emptyList())
    val viewed: StateFlow<List<Movie>> = _viewed

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history

    init {
        loadViewed()
        loadCollections()
        loadHistory()
    }

    private fun loadViewed() {
        viewModelScope.launch {
            // Заменить на вызов из репозитория
            _viewed.value = listOf(
//                Movie(1, "Дюна", "..."),
//                Movie(2, "Начало", "...")
            )
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            _collections.value = listOf(
                Collection(1, "Любимые", listOf()),
                Collection(2, "Хочу посмотреть", listOf())
            )
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _history.value = listOf(
                HistoryItem(1, "Кристофер Нолан", ItemType.PERSON),
                HistoryItem(2, "Интерстеллар", ItemType.MOVIE)
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _history.value = emptyList()
        }
    }
}