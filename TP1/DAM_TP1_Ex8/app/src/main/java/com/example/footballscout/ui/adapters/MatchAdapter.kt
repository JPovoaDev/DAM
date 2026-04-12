package com.example.footballscout.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footballscout.databinding.ItemMatchBinding
import com.example.footballscout.data.model.Match

class MatchAdapter : ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback) {

    inner class MatchViewHolder(private val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            binding.apply {
                matchOpponent.text = match.opponentName
                matchCompetition.text = match.competition
                matchDate.text = match.date
                
                if (match.homeScore.isNotEmpty() && match.awayScore.isNotEmpty()) {
                    matchScore.text = "${match.homeScore} - ${match.awayScore}"
                    matchScore.visibility = android.view.View.VISIBLE
                } else {
                    matchScore.visibility = android.view.View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean = oldItem == newItem
    }
}
