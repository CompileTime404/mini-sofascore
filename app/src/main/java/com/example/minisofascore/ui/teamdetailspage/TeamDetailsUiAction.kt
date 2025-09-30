package com.example.minisofascore.ui.teamdetailspage

interface TeamDetailsUiAction {
    data object ClickBackButton: TeamDetailsUiAction
    data class ClickPlayer(val playerId: Int): TeamDetailsUiAction
    data class ClickEvent(val eventId: Int): TeamDetailsUiAction
    data class ClickTournament(val tournamentId: Int): TeamDetailsUiAction
    data object Retry: TeamDetailsUiAction
}