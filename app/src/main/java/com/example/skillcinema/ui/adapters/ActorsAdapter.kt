package com.example.skillcinema.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.data.Actor
import com.example.skillcinema.databinding.ItemActorBinding

class ActorsAdapter(
    private val onActorClick: (Int) -> Unit
) : ListAdapter<Actor, ActorsAdapter.ActorViewHolder>(ActorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActorViewHolder(
        private val binding: ItemActorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: Actor) {
            with(binding) {
                Glide.with(root)
                    .load(actor.photoUrl)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
//                    .circleCrop()
                    .into(actorPhoto)

                actorName.text = actor.name
                actorRole.text = actor.role

                root.setOnClickListener { onActorClick(actor.id) }
            }
        }
    }

    class ActorDiffCallback : DiffUtil.ItemCallback<Actor>() {
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Actor, newItem: Actor) = oldItem == newItem
    }
}