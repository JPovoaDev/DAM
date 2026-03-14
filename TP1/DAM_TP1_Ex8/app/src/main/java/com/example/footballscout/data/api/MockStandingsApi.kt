package com.example.footballscout.data.api

import com.example.footballscout.data.api.model.StandingDto
import kotlinx.coroutines.delay

class MockStandingsApi {
    suspend fun getStandings(league: String): List<StandingDto> {
        delay(1000) // Simulate network delay
        return listOf(
            StandingDto(1, "Mock Team A", 85, 34, 26, 7, 1),
            StandingDto(2, "Mock Team B", 78, 34, 24, 6, 4),
            StandingDto(3, "Mock Team C", 72, 34, 21, 9, 4),
            StandingDto(4, "Mock Team D", 68, 34, 20, 8, 6),
            StandingDto(5, "Mock Team E", 60, 34, 17, 9, 8),
            StandingDto(6, "Mock Team F", 55, 34, 15, 10, 9)
        )
    }
}
