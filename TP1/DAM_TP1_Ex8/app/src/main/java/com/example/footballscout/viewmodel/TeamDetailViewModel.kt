package com.example.footballscout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.footballscout.data.api.model.StandingDto
import com.example.footballscout.data.repository.TeamRepository
import com.example.footballscout.data.model.Match
import com.example.footballscout.data.model.Team
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    private val repository: TeamRepository
) : ViewModel() {

    private val _teamState = MutableStateFlow<Resource<Team>>(Resource.Loading())
    val teamState: StateFlow<Resource<Team>> = _teamState

    private val _nextMatches = MutableStateFlow<Resource<List<Match>>>(Resource.Loading())
    val nextMatches: StateFlow<Resource<List<Match>>> = _nextMatches

    private val _previousMatches = MutableStateFlow<Resource<List<Match>>>(Resource.Loading())
    val previousMatches: StateFlow<Resource<List<Match>>> = _previousMatches

    private val _standings = MutableStateFlow<Resource<List<StandingDto>>>(Resource.Loading())
    val standings: StateFlow<Resource<List<StandingDto>>> = _standings

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadTeamData(teamId: String) {
        viewModelScope.launch {
            // Load Team details
            launch {
                repository.getTeamById(teamId).collect { resource ->
                    _teamState.value = resource
                    if (resource is Resource.Success && resource.data != null) {
                        // Trigger standings using the team's league name if needed (simulated)
                        loadStandings(resource.data.league)
                    }
                }
            }
            // Load Next Matches
            launch {
                repository.getNextMatches(teamId).collect {
                    _nextMatches.value = it
                }
            }
            // Load Previous Matches
            launch {
                repository.getPreviousMatches(teamId).collect {
                    _previousMatches.value = it
                }
            }
            // Favorite Status
            launch {
                repository.isTeamFavorite(teamId).collect {
                    _isFavorite.value = it
                }
            }
        }
    }

    private fun loadStandings(league: String) {
        viewModelScope.launch {
            repository.getLeagueStandings(league).collect {
                _standings.value = it
            }
        }
    }

    fun toggleFavorite(team: Team) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeTeamFromFavorites(team)
            } else {
                repository.addTeamToFavorites(team.copy(isFavorite = true))
            }
        }
    }

    class Factory(private val repository: TeamRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TeamDetailViewModel(repository) as T
        }
    }
}
