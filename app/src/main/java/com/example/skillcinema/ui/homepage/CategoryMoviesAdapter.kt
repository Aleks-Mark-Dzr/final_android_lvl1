package com.example.skillcinema.ui.homepage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.ItemCategoryBinding
import com.example.skillcinema.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso

class CategoryMoviesAdapter(
    private val categoryTitle: String,
    private val onMovieClick: (Movie) -> Unit,
    private val onShowAllClick: () -> Unit,
    private val showAllButton: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies: List<Movie> = emptyList()

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_SHOW_ALL = 1
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.nameRu
            binding.movieYear.text = movie.year
            binding.movieRating.text = "⭐ ${movie.ratingKinopoisk ?: "N/A"}"
            binding.movieGenres.text = if (movie.genres.isNotEmpty()) {
                movie.genres.joinToString(", ") { it.genre }
            } else {
                "Жанр неизвестен"
            }
            Picasso.get().load(movie.posterUrlPreview).into(binding.moviePoster)
            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    inner class ShowAllViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.categoryTitle.text = categoryTitle
            binding.showAllButton.visibility = View.VISIBLE
            binding.showAllButton.setOnClickListener { onShowAllClick() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showAllButton && position == movies.size) TYPE_SHOW_ALL else TYPE_MOVIE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MOVIE) {
            val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieViewHolder(binding)
        } else {
            val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ShowAllViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            holder.bind(movies[position])
        } else if (holder is ShowAllViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = if (showAllButton) movies.size + 1 else movies.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}