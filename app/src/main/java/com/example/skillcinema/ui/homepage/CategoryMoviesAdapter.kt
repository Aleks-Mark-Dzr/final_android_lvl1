package com.example.skillcinema.ui.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.ItemMovieBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CategoryMoviesAdapter(
    private val onMovieClick: (Int) -> Unit
) : ListAdapter<Movie, CategoryMoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.nameRu ?: "Неизвестно"
            binding.movieYear.text = movie.year ?: "--"
            binding.movieRating.text = "⭐ ${movie.ratingKinopoisk ?: "N/A"}"

            // ✅ Обрабатываем ошибки загрузки картинки
            Picasso.get().load(movie.posterUrlPreview)
                .error(com.example.skillcinema.R.drawable.placeholder_image) // Фолбэк изображение
                .into(binding.moviePoster, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    }
                })

            // ✅ Передаем `movie.kinopoiskId`
            binding.root.setOnClickListener { onMovieClick(movie.kinopoiskId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.kinopoiskId == newItem.kinopoiskId

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem == newItem
}