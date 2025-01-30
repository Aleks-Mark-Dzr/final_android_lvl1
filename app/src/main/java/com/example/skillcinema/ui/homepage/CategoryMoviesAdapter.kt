package com.example.skillcinema.ui.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.data.Movie
//import com.example.skillcinema.databinding.ItemCategoryBinding
import com.example.skillcinema.databinding.ItemMovieBinding

class CategoryMoviesAdapter(
    private val categoryTitle: String,
    private val movies: List<Movie> = emptyList(),
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<CategoryMoviesAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, onMovieClick: (Movie) -> Unit) {
            binding.movieTitle.text = movie.nameRu
            binding.movieYear.text = movie.year
            com.squareup.picasso.Picasso.get().load(movie.posterUrlPreview).into(binding.moviePoster)
            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position], onMovieClick)
    }

    override fun getItemCount(): Int = movies.size
}