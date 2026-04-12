package com.example.footballscout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.footballscout.data.repository.PlayerRepository
import com.example.footballscout.data.repository.TeamRepository
import com.example.footballscout.data.model.Player
import com.example.footballscout.data.model.Team
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    playerRepository: PlayerRepository,
    teamRepository: TeamRepository
) : ViewModel() {

    val favoritePlayers: StateFlow<List<Player>> = playerRepository.getFavoritePlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTeams: StateFlow<List<Team>> = teamRepository.getFavoriteTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(
        private val playerRepository: PlayerRepository,
        private val teamRepository: TeamRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(playerRepository, teamRepository) as T
        }
    }
}
