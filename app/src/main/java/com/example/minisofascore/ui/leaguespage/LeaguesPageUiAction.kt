package com.example.minisofascore.ui.leaguespage

interface LeaguesPageUiAction {
    data object ClickBackButton: LeaguesPageUiAction
    data class ClickTournament(val tournamentId: Int): LeaguesPageUiAction
    data class SelectSport(val sportSlug: String): LeaguesPageUiAction
    data object Retry: LeaguesPageUiAction
}