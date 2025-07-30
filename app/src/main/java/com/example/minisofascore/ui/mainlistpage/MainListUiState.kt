package com.example.minisofascore.ui.mainlistpage

import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Tournament
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

data class TournamentWithEvents(
    val tournament: Tournament,
    val events: ImmutableList<Event>
)

data class MainListUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedSportSlug: String = "football",
    val selectedDate: LocalDate = LocalDate.now(),
    val availableDates: ImmutableList<LocalDate> = persistentListOf(),
    val today: LocalDate = LocalDate.now(),
    val tournaments: ImmutableList<TournamentWithEvents> = persistentListOf(),
    val isEmpty: Boolean = true,
    val eventCount: Int? = null
)