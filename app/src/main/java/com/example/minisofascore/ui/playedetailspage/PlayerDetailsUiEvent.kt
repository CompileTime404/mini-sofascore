package com.example.minisofascore.ui.playedetailspage

sealed interface PlayerDetailsUiEvent {
    data object NavigateBack: PlayerDetailsUiEvent
    data class NavigateToTeamDetails(val teamId: Int): PlayerDetailsUiEvent
    data class NavigateToEventDetails(val eventId: Int): PlayerDetailsUiEvent
    data class NavigateToTournamentDetails(val tournamentId: Int): PlayerDetailsUiEvent
    data class ShowSnackbar(val message: String): PlayerDetailsUiEvent
}