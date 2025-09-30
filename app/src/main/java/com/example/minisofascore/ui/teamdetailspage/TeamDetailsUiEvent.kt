package com.example.minisofascore.ui.teamdetailspage

interface TeamDetailsUiEvent {
    data object NavigateBack : TeamDetailsUiEvent
    data class NavigateToPlayerDetails(val playerId: Int) : TeamDetailsUiEvent
    data class NavigateToEventDetails(val eventId: Int) : TeamDetailsUiEvent
    data class NavigateToTournamentDetails(val tournamentId: Int) : TeamDetailsUiEvent
    data class ShowSnackbar(val message: String) : TeamDetailsUiEvent
}