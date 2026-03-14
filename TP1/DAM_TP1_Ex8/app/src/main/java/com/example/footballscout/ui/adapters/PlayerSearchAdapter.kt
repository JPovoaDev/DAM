package com.example.footballscout.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footballscout.R
import com.example.footballscout.databinding.ItemPlayerBinding
import com.example.footballscout.data.model.Player

class PlayerSearchAdapter(
    private val onItemClick: (Player) -> Unit
) : ListAdapter<Player, PlayerSearchAdapter.PlayerViewHolder>(PlayerDiffCallback) {

    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(player: Player) {
            binding.apply {
                playerName.text = player.name
                playerTeam.text = player.team
                
                Glide.with(itemView.context)
                    .load(player.photoUrl)
                    .placeholder(R.drawable.ic_launcher_background) // We will use a proper placeholder later if needed
                    .circleCrop()
                    .into(playerImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }
}
