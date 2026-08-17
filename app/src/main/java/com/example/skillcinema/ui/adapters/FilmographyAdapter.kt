package com.example.skillcinema.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemFilmographyBinding
import com.example.skillcinema.domain.models.Film

/**
 * Строка списка фильмографии: постер с бейджем рейтинга, название и «год, жанр».
 * Постера и года в ответе api/v1/staff/{id} нет — карточка запрашивается по [onFilmShown]
 * в момент показа строки, поэтому для сотен фильмов не улетают сотни запросов.
 */
class FilmographyAdapter(
    private val onFilmShown: (Int) -> Unit,
    private val onFilmClick: (Int) -> Unit
) : ListAdapter<Film, FilmographyAdapter.FilmViewHolder>(FilmDiffCallback()) {

    inner class FilmViewHolder(
        private val binding: ItemFilmographyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) = with(binding) {
            filmTitle.text = film.title

            val subtitle = listOfNotNull(
                film.year.takeIf { it.isNotBlank() },
                film.genre?.takeIf { it.isNotBlank() }
            ).joinToString(", ")
            filmSubtitle.text = subtitle
            filmSubtitle.isVisible = subtitle.isNotBlank()

            filmRating.text = film.rating?.toString().orEmpty()
            filmRating.isVisible = film.rating != null

            Glide.with(filmPoster)
                .load(film.posterUrl)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(filmPoster)

            root.setOnClickListener { onFilmClick(film.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilmViewHolder(
        ItemFilmographyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
        if (film.posterUrl == null) onFilmShown(film.id)
    }

    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
    }
}
