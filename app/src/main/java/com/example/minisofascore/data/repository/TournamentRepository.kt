package com.example.minisofascore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Standings
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.data.paging.CombinedEventsPagingSource
import com.example.minisofascore.data.paging.EventSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TournamentRepository {
    suspend fun getTournament(id: Int): Tournament
    suspend fun getTournamentStandings(id: Int): Standings
    suspend fun getTournaments(slug: String): List<Tournament>
    fun getCombinedTournamentEvents(id: Int): Flow<PagingData<Event>>
}

class TournamentRepositoryImpl @Inject constructor(
    private val apiService: SofascoreApiService
) : TournamentRepository {
    override suspend fun getTournament(id: Int): Tournament {
        return apiService.getTournament(id)
    }

    override suspend fun getTournamentStandings(id: Int): Standings {
        return apiService.getTournamentStandings(id).firstOrNull()
            ?: throw Exception("No standings found")
    }

    override suspend fun getTournaments(slug: String): List<Tournament> {
        return apiService.getTournaments(slug)
    }

    override fun getCombinedTournamentEvents(
        id: Int
    ): Flow<PagingData<Event>> {
        val eventSource = EventSource.Tournament(id)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CombinedEventsPagingSource(apiService, eventSource) }
        ).flow
    }

}