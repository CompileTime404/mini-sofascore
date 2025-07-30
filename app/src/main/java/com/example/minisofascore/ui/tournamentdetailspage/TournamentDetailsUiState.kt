package com.example.minisofascore.ui.tournamentdetailspage

import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Standings
import com.example.minisofascore.data.model.Tournament

enum class TournamentTab(val label: String) {
    MATCHES("Matches"),
    STANDINGS("Standings")
}

sealed class DisplayItem {
    data class RoundHeaderItem(val round: Int) : DisplayItem()
    data class EventItem(val event: Event) : DisplayItem()
}


data class TournamentDetailsUiState(
    val isLoading: Boolean = true,
    val standings: Standings? = null,
    val selectedTab: TournamentTab = TournamentTab.MATCHES,
    val tournament: Tournament? = null,
    val errorMessage: String? = null
)