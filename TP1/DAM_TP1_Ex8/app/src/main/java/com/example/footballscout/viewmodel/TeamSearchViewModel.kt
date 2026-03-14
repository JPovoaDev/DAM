package com.example.footballscout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.footballscout.data.repository.TeamRepository
import com.example.footballscout.data.model.Team
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamSearchViewModel(
    private val repository: TeamRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Team>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<Team>>> = _searchState
    
    private var searchJob: Job? = null

    init {
        loadDefaultTeams()
    }

    private fun loadDefaultTeams() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            repository.getDefaultTeams().collect { result ->
                _searchState.value = result
            }
        }
    }

    fun searchTeams(query: String) {
        if (query.isBlank()) {
            loadDefaultTeams()
            return
        }
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            repository.searchTeams(query).collect { result ->
                _searchState.value = result
            }
        }
    }

    class Factory(private val repository: TeamRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TeamSearchViewModel(repository) as T
        }
    }
}
