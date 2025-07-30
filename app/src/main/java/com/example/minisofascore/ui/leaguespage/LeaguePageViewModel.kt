package com.example.minisofascore.ui.leaguespage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minisofascore.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguePageViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaguesPageUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LeaguePageUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        getTournaments(_uiState.value.selectedSportSlug)
    }

    fun onAction(action: LeaguesPageUiAction) {
        when (action) {
            LeaguesPageUiAction.ClickBackButton -> viewModelScope.launch {
                _uiEvent.emit(LeaguePageUiEvent.NavigateBack)
            }

            is LeaguesPageUiAction.ClickTournament -> viewModelScope.launch {
                _uiEvent.emit(LeaguePageUiEvent.NavigateToTournamentDetails(action.tournamentId))
            }

            is LeaguesPageUiAction.SelectSport -> viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedSportSlug = action.sportSlug
                    )
                }
                getTournaments(action.sportSlug)
            }
            LeaguesPageUiAction.Retry -> getTournaments(_uiState.value.selectedSportSlug)
        }
    }

    private fun getTournaments(slug: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                val tournamentList = tournamentRepository.getTournaments(slug)
                _uiState.update {
                    it.copy(
                        tournaments = tournamentList.toPersistentList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        errorMessage = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }
}