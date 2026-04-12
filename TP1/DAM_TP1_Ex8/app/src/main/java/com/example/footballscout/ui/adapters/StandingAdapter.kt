package com.example.footballscout.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footballscout.data.api.model.StandingDto
import com.example.footballscout.databinding.ItemStandingBinding

class StandingAdapter : ListAdapter<StandingDto, StandingAdapter.StandingViewHolder>(StandingDiffCallback) {

    inner class StandingViewHolder(private val binding: ItemStandingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: StandingDto) {
            binding.apply {
                positionText.text = standing.position.toString()
                teamNameText.text = standing.teamName
                ptsText.text = standing.points.toString()
                playedText.text = standing.playedMatches.toString()
                wText.text = standing.won.toString()
                dText.text = standing.draw.toString()
                lText.text = standing.lost.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val binding = ItemStandingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object StandingDiffCallback : DiffUtil.ItemCallback<StandingDto>() {
        override fun areItemsTheSame(oldItem: StandingDto, newItem: StandingDto): Boolean = oldItem.teamName == newItem.teamName
        override fun areContentsTheSame(oldItem: StandingDto, newItem: StandingDto): Boolean = oldItem == newItem
    }
}
