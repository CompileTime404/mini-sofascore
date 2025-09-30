package com.example.minisofascore.ui.teamdetailspage

import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TeamDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val team: Team3? = null,
    val events: ImmutableList<Event> = persistentListOf(),
    val players: ImmutableList<Player> = persistentListOf(),
    val tournaments: ImmutableList<Tournament> = persistentListOf()
)
