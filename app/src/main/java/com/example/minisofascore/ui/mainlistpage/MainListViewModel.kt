package com.example.minisofascore.ui.mainlistpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.repository.EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class MainListViewModel @Inject constructor(
    private val eventsRepository: EventsRepository
) : ViewModel() {

    private val _selectedSportSlug = MutableStateFlow("football")
    val selectedSportSlug = _selectedSportSlug.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val events: StateFlow<List<Event>> = combine(
        _selectedSportSlug,
        _selectedDate
    ) { slug, date ->
        slug to date
    }.flatMapLatest { (slug, date) ->
        _isLoading.value = true
        eventsRepository.getEvents(slug, date.toString())
            .onEach { _isLoading.value = false }
    }.distinctUntilChanged()
        .catch { e ->
            emit(persistentListOf())
            viewModelScope.launch {
                _uiEvent.emit(MainListUiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )

    val uiState: StateFlow<MainListUiState> = combine(
        _selectedSportSlug,
        _selectedDate,
        _isLoading,
        events
    ) { slug, date, isLoading, events  ->
        val today = LocalDate.now()
        MainListUiState(
            isLoading = isLoading,
            errorMessage = null,
            selectedSportSlug = slug,
            selectedDate = date,
            availableDates = availableDates(today),
            today = today,
            tournaments = groupEventsByTournament(events).toImmutableList(),
            isEmpty = events.isEmpty(),
            eventCount = events.count()
        )
    }.distinctUntilChanged()
        .catch { e ->
            emit(
                MainListUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error",
                    selectedSportSlug = _selectedSportSlug.value,
                    selectedDate = _selectedDate.value,
                    today = LocalDate.now(),
                    availableDates = availableDates(LocalDate.now())
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainListUiState(isLoading = true)
        )

    private val _uiEvent = MutableSharedFlow<MainListUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: MainListUiAction) {
        when (action) {
            is MainListUiAction.ClickEvent -> emitEvent(
                MainListUiEvent.NavigateToEventDetails(
                    action.eventId
                )
            )

            MainListUiAction.ClickLeagues -> emitEvent(MainListUiEvent.NavigateToLeagues)

            MainListUiAction.ClickSettings -> emitEvent(MainListUiEvent.NavigateToSettings)

            is MainListUiAction.ClickTournament -> emitEvent(
                MainListUiEvent.NavigateToTournamentDetails(
                    action.tournamentId
                )
            )

            MainListUiAction.Retry -> _selectedDate.update { it }

            is MainListUiAction.SelectDate -> _selectedDate.value = action.date

            is MainListUiAction.SelectSport -> _selectedSportSlug.value = action.sportSlug
        }
    }


    private fun emitEvent(event: MainListUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun groupEventsByTournament(events: List<Event>): List<TournamentWithEvents> {
        return events.groupBy { it.tournament }.map { (tournament, events) ->
            TournamentWithEvents(tournament, events.toImmutableList())
        }
    }

    private fun availableDates(today: LocalDate): ImmutableList<LocalDate> {
        return (-30L..30L).map {
            today.plusDays(it)
        }.toImmutableList()
    }
}
