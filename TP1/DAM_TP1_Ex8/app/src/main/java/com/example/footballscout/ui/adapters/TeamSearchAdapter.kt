package com.example.footballscout.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footballscout.R
import com.example.footballscout.databinding.ItemTeamBinding
import com.example.footballscout.data.model.Team

class TeamSearchAdapter(
    private val onItemClick: (Team) -> Unit
) : ListAdapter<Team, TeamSearchAdapter.TeamViewHolder>(TeamDiffCallback) {

    inner class TeamViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(team: Team) {
            binding.apply {
                teamName.text = team.name
                teamLeague.text = team.league
                binding.teamTitles.text = team.titles
                
                Glide.with(itemView.context)
                    .load(team.logoUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(teamLogo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object TeamDiffCallback : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem == newItem
        }
    }
}
