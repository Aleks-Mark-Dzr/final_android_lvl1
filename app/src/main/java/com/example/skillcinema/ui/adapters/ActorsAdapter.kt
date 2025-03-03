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
//import com.example.skillcinema.domain.models.Actor

class ActorsAdapter(
    private val onActorClick: (Int) -> Unit,
    private val onAllActorsClick: (() -> Unit)? = null
) : ListAdapter<Actor, RecyclerView.ViewHolder>(ActorDiffCallback()) {

    companion object {
        private const val TYPE_ACTOR = 0
        private const val TYPE_SHOW_ALL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACTOR -> ActorViewHolder(
                ItemActorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ShowAllViewHolder(
                ItemActorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActorViewHolder -> holder.bind(getItem(position))
            is ShowAllViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) TYPE_ACTOR else TYPE_SHOW_ALL
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (onAllActorsClick != null) 1 else 0
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
                    .circleCrop()
                    .into(actorPhoto)

                actorName.text = actor.name
                actorRole.text = actor.role

                root.setOnClickListener { onActorClick(actor.id) }
            }
        }
    }

    inner class ShowAllViewHolder(
        private val binding: ItemActorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            with(binding) {
                actorPhoto.setImageResource(R.drawable.ic_more)
                actorName.text = ""
                actorRole.text = ""
                root.setOnClickListener { onAllActorsClick?.invoke() }
            }
        }
    }

    class ActorDiffCallback : DiffUtil.ItemCallback<Actor>() {
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Actor, newItem: Actor) = oldItem == newItem
    }
}