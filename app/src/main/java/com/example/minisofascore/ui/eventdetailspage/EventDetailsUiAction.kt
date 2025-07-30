package com.example.minisofascore.ui.eventdetailspage

sealed interface EventDetailsUiAction {
    data object ClickBackButton : EventDetailsUiAction
    data class ClickTournament(val tournamentId: Int) : EventDetailsUiAction
    data class ClickTeam(val teamId: Int) : EventDetailsUiAction
    data class ClickPlayer(val playerId: Int) : EventDetailsUiAction
}