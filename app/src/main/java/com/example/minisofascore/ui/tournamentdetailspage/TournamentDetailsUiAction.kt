package com.example.minisofascore.ui.tournamentdetailspage

sealed interface TournamentDetailsUiAction {
    data object ClickBackButton: TournamentDetailsUiAction
    data class ClickTeam(val teamId: Int): TournamentDetailsUiAction
    data class ClickEvent(val eventId: Int): TournamentDetailsUiAction
    data class SelectTab(val tab: TournamentTab): TournamentDetailsUiAction
}