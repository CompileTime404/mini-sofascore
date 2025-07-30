package com.example.minisofascore.ui.playedetailspage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.repository.PlayerRepository
import com.example.minisofascore.ui.playedetailspage.PlayerDetailsUiEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playerId: Int = checkNotNull(savedStateHandle["playerId"])

    private val _uiState = MutableStateFlow(PlayerDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PlayerDetailsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val listOfEvents: Flow<PagingData<Event>> =
        playerRepository.getPlayerEvents(playerId).cachedIn(viewModelScope)

    init {
        getPlayer()
    }

    fun onAction(action: PlayerDetailsUiAction) {
        when (action) {
            PlayerDetailsUiAction.ClickBackButton -> emitEvent(NavigateBack)
            is PlayerDetailsUiAction.ClickEvent -> emitEvent(NavigateToEventDetails(action.eventId))
            is PlayerDetailsUiAction.ClickTeam -> emitEvent(NavigateToTeamDetails(action.teamId))
            is PlayerDetailsUiAction.ClickTournament -> emitEvent(NavigateToTournamentDetails(action.tournamentId))
        }
    }

    private fun emitEvent(event: PlayerDetailsUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun getPlayer() {
        viewModelScope.launch {
            try {
                val player = playerRepository.getPlayer(playerId)
                _uiState.update {
                    it.copy(player = player, isLoading = false)
                }
                val tournaments =
                    playerRepository.getTeamTournaments(uiState.value.player?.team?.id ?: -1)
                _uiState.update {
                    it.copy(tournament = tournaments.firstOrNull(), isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }
}

















