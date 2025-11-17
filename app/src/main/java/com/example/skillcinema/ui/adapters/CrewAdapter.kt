package com.example.skillcinema.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.data.CrewMember
import com.example.skillcinema.databinding.ItemStaffBinding


class CrewAdapter(
    private val onCrewClick: (Int) -> Unit
) : ListAdapter<CrewMember, CrewAdapter.CrewViewHolder>(CrewDiffCallback) {

    class CrewViewHolder(private val binding: ItemStaffBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: CrewMember) = with(binding) {
            // Загрузка фотографии участника
            Glide.with(root)
                .load(member.photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .centerCrop()
                .into(staffPhoto)
            // Установка имени и роли
            staffName.text = member.name
            profession.text = member.professionText?.ifBlank { member.role } ?: member.role
            // Обработка клика по элементу
//            root.setOnClickListener { onCrewClick(member.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val binding = ItemStaffBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// DiffUtil для сравнения элементов по id (для эффективных обновлений списка)
object CrewDiffCallback : DiffUtil.ItemCallback<CrewMember>() {
    override fun areItemsTheSame(oldItem: CrewMember, newItem: CrewMember) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CrewMember, newItem: CrewMember) = oldItem == newItem
}