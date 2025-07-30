package com.example.minisofascore.data.repository

import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.Incident
import com.example.minisofascore.util.pollingFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface IncidentsRepository {
    fun getEvent(id: Int): Flow<Event>
    fun getEventIncidents(id: Int): Flow<List<Incident>>
}

class IncidentsRepositoryImpl @Inject constructor(
    private val apiService: SofascoreApiService
) : IncidentsRepository{
    override fun getEvent(id: Int): Flow<Event> {
        return pollingFlow {
            apiService.getEvent(id)
        }
    }

    override fun getEventIncidents(id: Int): Flow<List<Incident>> {
        return pollingFlow {
            apiService.getEventIncidents(id)
        }
    }
}