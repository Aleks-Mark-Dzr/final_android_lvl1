package com.example.skillcinema.ui.moviedetail.collections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemMovieCollectionBinding

data class MovieCollectionItemUi(
    val id: Int,
    val name: String,
    val count: Int,
    val isChecked: Boolean
)

class MovieCollectionsAdapter(
    private val onCheckedChange: (MovieCollectionItemUi, Boolean) -> Unit
) : ListAdapter<MovieCollectionItemUi, MovieCollectionsAdapter.CollectionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemMovieCollectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionViewHolder(
        private val binding: ItemMovieCollectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieCollectionItemUi) {
            binding.cbCollection.setOnCheckedChangeListener(null)
            binding.cbCollection.text = item.name
            binding.cbCollection.isChecked = item.isChecked
            binding.tvCollectionCount.text = item.count.toString()
            binding.cbCollection.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(item, isChecked)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieCollectionItemUi>() {
        override fun areItemsTheSame(
            oldItem: MovieCollectionItemUi,
            newItem: MovieCollectionItemUi
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MovieCollectionItemUi,
            newItem: MovieCollectionItemUi
        ): Boolean = oldItem == newItem
    }
}