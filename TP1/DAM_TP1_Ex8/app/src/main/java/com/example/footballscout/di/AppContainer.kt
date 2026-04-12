package com.example.footballscout.di

import android.content.Context
import com.example.footballscout.data.api.MockStandingsApi
import com.example.footballscout.data.api.RetrofitClient
import com.example.footballscout.data.database.AppDatabase
import com.example.footballscout.data.repository.PlayerRepository
import com.example.footballscout.data.repository.TeamRepository

class AppContainer(private val context: Context) {
    
    // Database
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    
    // API
    private val sportsDbApi = RetrofitClient.theSportsDbApi
    private val standingsApi = MockStandingsApi()
    
    // Repositories
    val playerRepository: PlayerRepository by lazy {
        PlayerRepository(sportsDbApi, database.playerDao())
    }
    
    val teamRepository: TeamRepository by lazy {
        TeamRepository(sportsDbApi, standingsApi, database.teamDao())
    }
}
