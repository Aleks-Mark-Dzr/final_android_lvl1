package com.example.skillcinema.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemCollectionBinding
import com.example.skillcinema.databinding.ItemFavoriteCollectionBinding
import com.example.skillcinema.data.Collection

class CollectionAdapter(
    private val onClick: (Collection) -> Unit
) : ListAdapter<Collection, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_FAVORITE = 0
        private const val TYPE_DEFAULT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == 1) TYPE_FAVORITE else TYPE_DEFAULT
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_FAVORITE) {
            val binding = ItemFavoriteCollectionBinding.inflate(inflater, parent, false)
            FavoriteCollectionViewHolder(binding)
        } else {
            val binding = ItemCollectionBinding.inflate(inflater, parent, false)
            CollectionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is FavoriteCollectionViewHolder -> holder.bind(item)
            is CollectionViewHolder -> holder.bind(item)
        }
    }

    inner class FavoriteCollectionViewHolder(
        private val binding: ItemFavoriteCollectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collection: Collection) {
            binding.root.setOnClickListener { onClick(collection) }
        }
    }

    inner class CollectionViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: Collection) {
            binding.tvCollectionName.text = collection.name
            binding.root.setOnClickListener {
                onClick(collection)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean =
            oldItem == newItem
    }
}