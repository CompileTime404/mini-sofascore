package com.example.minisofascore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Span
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.data.paging.CombinedEventsPagingSource
import com.example.minisofascore.data.paging.EventSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TeamRepository {
    suspend fun getTeam(id: Int): Team3
    fun getTeamEvents(id: Int): Flow<PagingData<Event>>
    suspend fun getTeamPlayers(id: Int): List<Player>
    suspend fun getTeamTournaments(id : Int): List<Tournament>
}

class TeamRepositoryImpl @Inject constructor(
    private val apiService: SofascoreApiService
) : TeamRepository {
    override suspend fun getTeam(id: Int): Team3 {
        return apiService.getTeam(id)
    }

    override fun getTeamEvents(id: Int): Flow<PagingData<Event>> {
        val eventSource = EventSource.Team(id)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CombinedEventsPagingSource(apiService, eventSource) }
        ).flow
    }

    override suspend fun getTeamPlayers(id: Int): List<Player> {
        return apiService.getTeamPlayers(id)
    }

    override suspend fun getTeamTournaments(id: Int): List<Tournament> {
        return apiService.getTeamTournaments(id)

    }
}