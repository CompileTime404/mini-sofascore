package com.example.minisofascore.ui.eventdetailspage

import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Incident
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class EventDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val event: Event? = null,
    val eventIncidents: ImmutableList<Incident> = persistentListOf(),
    val eventStatus: EventStatusEnum = EventStatusEnum.NOT_STARTED
)