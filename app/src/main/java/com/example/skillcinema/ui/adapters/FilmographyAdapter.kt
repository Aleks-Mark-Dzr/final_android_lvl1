package com.example.skillcinema.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemFilmographyBinding
import com.example.skillcinema.domain.models.Film

/**
 * Строка списка фильмографии: рейтинг, название и роль.
 * Постера и года в ответе api/v1/staff/{id} нет, а подгружать карточку под каждый
 * фильм из нескольких сотен слишком дорого, поэтому список текстовый.
 */
class FilmographyAdapter(
    private val onFilmClick: (Int) -> Unit
) : ListAdapter<Film, FilmographyAdapter.FilmViewHolder>(FilmDiffCallback()) {

    inner class FilmViewHolder(
        private val binding: ItemFilmographyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) = with(binding) {
            filmTitle.text = film.title

            filmRole.text = film.role.orEmpty()
            filmRole.isVisible = !film.role.isNullOrBlank()

            filmRating.text = film.rating?.toString() ?: "–"

            root.setOnClickListener { onFilmClick(film.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilmViewHolder(
        ItemFilmographyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
    }
}
