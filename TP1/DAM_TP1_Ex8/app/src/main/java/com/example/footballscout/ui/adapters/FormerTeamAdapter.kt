package com.example.footballscout.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footballscout.R
import com.example.footballscout.data.api.model.FormerTeamDto
import com.example.footballscout.databinding.ItemFormerTeamBinding

class FormerTeamAdapter : ListAdapter<FormerTeamDto, FormerTeamAdapter.FormerTeamViewHolder>(FormerTeamDiffCallback) {

    inner class FormerTeamViewHolder(private val binding: ItemFormerTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(team: FormerTeamDto) {
            binding.apply {
                formerTeamName.text = team.strFormerTeam ?: ""
                
                Glide.with(itemView.context)
                    .load(team.strBadge)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(formerTeamLogo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormerTeamViewHolder {
        val binding = ItemFormerTeamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FormerTeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormerTeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object FormerTeamDiffCallback : DiffUtil.ItemCallback<FormerTeamDto>() {
        override fun areItemsTheSame(oldItem: FormerTeamDto, newItem: FormerTeamDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FormerTeamDto, newItem: FormerTeamDto): Boolean {
            return oldItem == newItem
        }
    }
}
