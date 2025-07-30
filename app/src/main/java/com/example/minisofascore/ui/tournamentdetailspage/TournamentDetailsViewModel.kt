package com.example.minisofascore.ui.tournamentdetailspage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.minisofascore.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tournamentId: Int = checkNotNull(savedStateHandle["tournamentId"])

    private val _uiState = MutableStateFlow(TournamentDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TournamentDetailsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val listOfDisplayItems: Flow<PagingData<DisplayItem>> =
        tournamentRepository.getCombinedTournamentEvents(tournamentId)
            .map { pagingData ->
                pagingData.map { DisplayItem.EventItem(it) }
                    .insertSeparators { before, after ->
                        val beforeRound = before?.event?.round
                        val afterRound = after?.event?.round

                        if (afterRound != null && beforeRound != afterRound) {
                            DisplayItem.RoundHeaderItem(afterRound)
                        } else {
                            null
                        }
                    }
            }
            .cachedIn(viewModelScope)


    init {
        getTournament(tournamentId)
    }

    fun onAction(action: TournamentDetailsUiAction) {
        when (action) {
            TournamentDetailsUiAction.ClickBackButton -> emitEvent(TournamentDetailsUiEvent.NavigateBack)
            is TournamentDetailsUiAction.ClickEvent -> emitEvent(
                TournamentDetailsUiEvent.NavigateToEventDetails(
                    action.eventId
                )
            )

            is TournamentDetailsUiAction.ClickTeam -> emitEvent(
                TournamentDetailsUiEvent.NavigateToTeamDetails(
                    action.teamId
                )
            )

            is TournamentDetailsUiAction.SelectTab -> {
                _uiState.update { it.copy(selectedTab = action.tab) }
                if (action.tab == TournamentTab.STANDINGS) {
                    getTournamentStandings(tournamentId)
                }
            }
        }
    }

    private fun emitEvent(event: TournamentDetailsUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun getTournament(tournamentId: Int) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournament(tournamentId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    tournament = tournament
                )
            }
        }
    }

    private fun getTournamentStandings(tournamentId: Int) {
        viewModelScope.launch {
            val standings = tournamentRepository.getTournamentStandings(tournamentId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    standings = standings
                )
            }
        }
    }
}