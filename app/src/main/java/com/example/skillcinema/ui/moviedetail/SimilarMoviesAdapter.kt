package com.example.skillcinema.ui.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.GlideApp
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.ItemSimilarMovieBinding

class SimilarMoviesAdapter(
    private val onMovieClick: (Int) -> Unit
) : ListAdapter<Movie, SimilarMoviesAdapter.SimilarMovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding = ItemSimilarMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SimilarMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimilarMovieViewHolder(
        private val binding: ItemSimilarMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            with(binding) {
                GlideApp.with(root)
                    .load(movie.posterUrlPreview)
                    .into(ivSimilarPoster)

                tvSimilarTitle.text = movie.nameRu ?: ""
                tvSimilarYear.text = movie.year

                root.setOnClickListener {
                    onMovieClick(movie.kinopoiskId)
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.kinopoiskId == newItem.kinopoiskId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}