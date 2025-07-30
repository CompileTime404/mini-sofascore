package com.example.minisofascore.ui.eventdetailspage

sealed interface EventDetailsUiEvent {
    data object NavigateBack : EventDetailsUiEvent
    data class NavigateToTeamDetails(val teamId: Int) : EventDetailsUiEvent
    data class NavigateToPlayerDetails(val playerId: Int) : EventDetailsUiEvent
    data class NavigateToTournamentDetails(val tournamentId: Int) : EventDetailsUiEvent
    data class ShowSnackbar(val message: String) : EventDetailsUiEvent
}