package com.example.skillcinema.ui.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.GlideApp
import com.example.skillcinema.R
import com.example.skillcinema.data.GalleryItem
import com.example.skillcinema.databinding.ItemPhotoBinding

class GalleryAdapter : ListAdapter<GalleryItem, GalleryAdapter.GalleryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GalleryViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryItem) {
            GlideApp.with(binding.ivPhoto.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.ivPhoto)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
            oldItem.imageUrl == newItem.imageUrl

        override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
            oldItem == newItem
    }
}