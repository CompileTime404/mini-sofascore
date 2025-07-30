package com.example.minisofascore.ui.tournamentdetailspage

sealed interface TournamentDetailsUiEvent {
    data object NavigateBack : TournamentDetailsUiEvent
    data class NavigateToEventDetails(val eventId: Int) : TournamentDetailsUiEvent
    data class NavigateToTeamDetails(val teamId: Int) : TournamentDetailsUiEvent
    data class ShowSnackbar(val message: String) : TournamentDetailsUiEvent
}