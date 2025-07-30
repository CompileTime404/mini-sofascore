package com.example.minisofascore.ui.mainlistpage

import java.time.LocalDate

sealed interface MainListUiAction {
    data object ClickLeagues : MainListUiAction
    data object ClickSettings : MainListUiAction
    data class ClickTournament(val tournamentId: Int) : MainListUiAction
    data class ClickEvent(val eventId: Int) : MainListUiAction
    data class SelectSport(val sportSlug: String) : MainListUiAction
    data class SelectDate(val date: LocalDate) : MainListUiAction
    data object Retry : MainListUiAction
}