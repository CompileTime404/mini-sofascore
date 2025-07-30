package com.example.minisofascore.ui.mainlistpage

sealed interface MainListUiEvent {
    data object NavigateToLeagues : MainListUiEvent
    data object NavigateToSettings : MainListUiEvent
    data class NavigateToTournamentDetails(val tournamentId: Int) : MainListUiEvent
    data class NavigateToEventDetails(val eventId: Int) : MainListUiEvent
    data class ShowSnackbar(val message: String) : MainListUiEvent
}