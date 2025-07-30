package com.example.minisofascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StandingsTypeEnum{
    @SerialName("total") TOTAL,
    @SerialName("home") HOME,
    @SerialName("away") AWAY
}

@Serializable
data class Standings(
    val id: Int,
    val tournament: Tournament,
    val type: StandingsTypeEnum,
    val sortedStandingsRows: List<SortedStandingsRow>
)

@Serializable
data class SortedStandingsRow(
    val id: Int,
    val team: Team3,
    val points: Int? = null,
    val scoresFor: Int,
    val scoresAgainst: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val percentage: Float? = null
)