package com.example.minisofascore.ui.teamdetailspage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository
) : ViewModel() {
    val teamId: Int = checkNotNull(savedStateHandle["teamId"])

    private val _uiState = MutableStateFlow(TeamDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TeamDetailsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val teamEvents: Flow<PagingData<Event>> =
        teamRepository.getTeamEvents(teamId).cachedIn(viewModelScope)

    init {
        getTeam(teamId)
        getTournaments(teamId)
        getTeamPlayers(teamId)
    }

    fun onAction(action: TeamDetailsUiAction) {
        when (action) {
            TeamDetailsUiAction.ClickBackButton -> {
                emitEvent(TeamDetailsUiEvent.NavigateBack)
            }
            is TeamDetailsUiAction.ClickPlayer -> {
                emitEvent(TeamDetailsUiEvent.NavigateToPlayerDetails(action.playerId))
            }
            is TeamDetailsUiAction.ClickEvent -> {
                emitEvent(TeamDetailsUiEvent.NavigateToEventDetails(action.eventId))
            }
            is TeamDetailsUiAction.ClickTournament -> {
                emitEvent(TeamDetailsUiEvent.NavigateToTournamentDetails(action.tournamentId))
            }
            TeamDetailsUiAction.Retry -> {
                getTeam(teamId)
                getTeamPlayers(teamId)
                getTournaments(teamId)
            }
        }
    }

    fun emitEvent(event: TeamDetailsUiEvent){
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun getTeam(id: Int) {
        viewModelScope.launch {
            val team = teamRepository.getTeam(id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    team = team
                )
            }
        }
    }

    private fun getTeamPlayers(id: Int){
        viewModelScope.launch {
            val players = teamRepository.getTeamPlayers(id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    players = players.toImmutableList()
                )
            }
        }
    }

    private fun getTournaments(id: Int){
        viewModelScope.launch {
            val tournaments = teamRepository.getTeamTournaments(id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    tournaments = tournaments.toImmutableList()
                )
            }
        }
    }
}
