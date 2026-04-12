package com.example.footballscout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.footballscout.data.repository.PlayerRepository
import com.example.footballscout.data.model.Player
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val repository: PlayerRepository
) : ViewModel() {

    private val _playerState = MutableStateFlow<Resource<Player>>(Resource.Loading())
    val playerState: StateFlow<Resource<Player>> = _playerState

    // To hold the favorite status locally for fast UI updates
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private var loadJob: kotlinx.coroutines.Job? = null

    fun loadPlayer(playerId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            launch {
                repository.getPlayerById(playerId).collect { resource ->
                    _playerState.value = resource
                }
            }
            // Also listen to favorite status
            launch {
                repository.isPlayerFavorite(playerId).collect { isFav ->
                    _isFavorite.value = isFav
                }
            }
        }
    }

    fun toggleFavorite(player: Player) {
        viewModelScope.launch {
            val current = _isFavorite.value
            if (current) {
                repository.removePlayerFromFavorites(player)
            } else {
                repository.addPlayerToFavorites(player.copy(isFavorite = true))
            }
            // Realistically we also listen to the DB for true source of truth
        }
    }

    class Factory(private val repository: PlayerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerDetailViewModel(repository) as T
        }
    }
}
