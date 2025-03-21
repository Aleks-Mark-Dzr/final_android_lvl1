package com.example.skillcinema.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.GlideApp
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.ItemFilmBinding

//import com.example.skillcinema.data.model.Movie

class SearchAdapter(private val onMovieClick: (Movie) -> Unit) :
    ListAdapter<Movie, SearchAdapter.SearchViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemFilmBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchViewHolder(private val binding: ItemFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.filmTitle.text = movie.nameRu ?: "Без названия"
            binding.filmYear.text = movie.year?.toString() ?: ""
            binding.filmGenres.text = movie.genres?.joinToString { it.genre } ?: ""

            GlideApp.with(binding.root)
                .load(movie.posterUrlPreview)
                .into(binding.filmPoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }


    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.kinopoiskId == newItem.kinopoiskId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}