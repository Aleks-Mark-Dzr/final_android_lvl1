// ui/adapters/FilmsAdapter.kt
package com.example.skillcinema.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemMovieBinding
import com.example.skillcinema.domain.models.Film

class FilmsAdapter(
    private val onFilmClick: (Int) -> Unit
) : ListAdapter<Film, FilmsAdapter.FilmViewHolder>(FilmDiffCallback()) {

    inner class FilmViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) {
            with(binding) {
                Glide.with(root)
                    .load(film.posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .into(moviePoster)

                movieTitle.text = film.title
                movieYear.text = film.year
                movieRating.text = film.rating?.toString() ?: "-"

                root.setOnClickListener { onFilmClick(film.id) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilmViewHolder(
        ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
    }
}