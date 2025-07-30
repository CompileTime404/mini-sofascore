package com.example.minisofascore.ui.eventdetailspage

import android.util.Log.e
import android.util.Log.v
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.Incident
import com.example.minisofascore.data.repository.IncidentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Boolean

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val incidentsRepository: IncidentsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: Int = checkNotNull(savedStateHandle["eventId"])

    val uiState: StateFlow<EventDetailsUiState> = combine(
        incidentsRepository.getEvent(eventId),
        incidentsRepository.getEventIncidents(eventId)
    ) { event, incidents ->
        EventDetailsUiState(
            isLoading = false,
            event = event,
            eventStatus = event.status,
            eventIncidents = incidents.toPersistentList(),
            errorMessage = null
        )
    }
        .distinctUntilChanged()
        .catch { e ->
            emit(EventDetailsUiState(errorMessage = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EventDetailsUiState(isLoading = true)
        )

    private val _uiEvent = MutableSharedFlow<EventDetailsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: EventDetailsUiAction) {
        when (action) {
            EventDetailsUiAction.ClickBackButton -> emitEvent(EventDetailsUiEvent.NavigateBack)

            is EventDetailsUiAction.ClickPlayer -> emitEvent(
                EventDetailsUiEvent.NavigateToPlayerDetails(
                    action.playerId
                )
            )

            is EventDetailsUiAction.ClickTeam -> emitEvent(
                EventDetailsUiEvent.NavigateToTeamDetails(
                    action.teamId
                )
            )

            is EventDetailsUiAction.ClickTournament -> emitEvent(
                EventDetailsUiEvent.NavigateToTournamentDetails(
                    action.tournamentId
                )
            )
        }
    }

    private fun emitEvent(event: EventDetailsUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}