package com.example.footballscout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.footballscout.data.repository.PlayerRepository
import com.example.footballscout.data.model.Player
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerSearchViewModel(
    private val repository: PlayerRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Player>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<Player>>> = _searchState
    
    private var searchJob: Job? = null
    private var lastQuery: String = "Ronaldo"

    init {
        searchPlayers("Ronaldo")
    }

    fun refreshSearch() {
        searchPlayers(lastQuery)
    }

    fun searchPlayers(query: String) {
        val effectiveQuery = if (query.isBlank()) "Ronaldo" else query
        lastQuery = effectiveQuery
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            repository.searchPlayers(effectiveQuery).collect { result ->
                _searchState.value = result
            }
        }
    }

    class Factory(private val repository: PlayerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerSearchViewModel(repository) as T
        }
    }
}
