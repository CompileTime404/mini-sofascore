package com.example.minisofascore.data.repository

import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.util.pollingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface EventsRepository {
    fun getEvents(slug: String, date: String): Flow<List<Event>>
}

class EventsRepositoryImpl @Inject constructor(
    private val apiService: SofascoreApiService
) : EventsRepository {
    override fun getEvents(
        slug: String,
        date: String
    ): Flow<List<Event>> {
        return flow {
            emit(apiService.getEvents(slug, date))
        }
    }
}