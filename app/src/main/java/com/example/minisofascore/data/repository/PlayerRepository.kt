package com.example.minisofascore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.data.paging.CombinedEventsPagingSource
import com.example.minisofascore.data.paging.EventSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PlayerRepository {
    suspend fun getPlayer(id: Int): Player
    suspend fun getTeamTournaments(id: Int): List<Tournament>
    fun getPlayerEvents(id: Int): Flow<PagingData<Event>>
}

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: SofascoreApiService
): PlayerRepository {
    override suspend fun getPlayer(id: Int): Player {
        return apiService.getPlayer(id)
    }

    override suspend fun getTeamTournaments(id: Int): List<Tournament> {
        return apiService.getTeamTournaments(id)
    }

    override fun getPlayerEvents(
        id: Int
    ): Flow<PagingData<Event>> {
        val eventSource = EventSource.Player(id)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CombinedEventsPagingSource(apiService, eventSource) }
        ).flow
    }
}