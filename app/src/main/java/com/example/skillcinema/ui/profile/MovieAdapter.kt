package com.example.skillcinema.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.ItemMovieBinding
import androidx.core.view.isVisible

class MovieAdapter(
    private val onClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.nameRu
            binding.movieYear.text = movie.year
            binding.movieYear.isVisible = movie.year.isNotBlank()
            binding.movieRating.text = movie.ratingKinopoisk?.toString() ?: ""
            binding.movieRating.isVisible = !binding.movieRating.text.isNullOrBlank()
            binding.movieGenres.text = movie.genres.joinToString(", ") { it.genre }
            binding.movieGenres.isVisible = binding.movieGenres.text.isNotBlank()
            Glide.with(binding.moviePoster.context)
                .load(movie.posterUrlPreview)
                .into(binding.moviePoster)

            binding.root.setOnClickListener {
                onClick(movie)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.kinopoiskId == newItem.kinopoiskId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}