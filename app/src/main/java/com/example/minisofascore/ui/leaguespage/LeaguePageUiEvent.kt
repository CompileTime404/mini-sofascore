package com.example.minisofascore.ui.leaguespage

interface LeaguePageUiEvent {
    data object NavigateBack: LeaguePageUiEvent
    data class NavigateToTournamentDetails(val tournamentId: Int): LeaguePageUiEvent
    data class ShowSnackbar(val message: String): LeaguePageUiEvent
}