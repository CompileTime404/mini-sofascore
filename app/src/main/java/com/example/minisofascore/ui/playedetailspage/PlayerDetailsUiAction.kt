package com.example.minisofascore.ui.playedetailspage

sealed interface PlayerDetailsUiAction {
    data object ClickBackButton : PlayerDetailsUiAction
    data class ClickTeam(val teamId: Int): PlayerDetailsUiAction
    data class ClickTournament(val tournamentId: Int): PlayerDetailsUiAction
    data class ClickEvent(val eventId: Int): PlayerDetailsUiAction
}