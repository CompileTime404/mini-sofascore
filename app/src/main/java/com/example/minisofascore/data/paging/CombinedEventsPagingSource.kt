package com.example.minisofascore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Span

class CombinedEventsPagingSource(
    private val api: SofascoreApiService,
    private val eventSource: EventSource
) : PagingSource<Int, Event>() {

    private var hasLoadedNext = false

    override fun getRefreshKey(state: PagingState<Int, Event>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return try {
            val page = params.key ?: 0

            val events = when (eventSource) {
                is EventSource.Tournament -> {
                    if (page == 0 && !hasLoadedNext){
                        val next = api.getTournamentEvents(eventSource.tournamentId, Span.NEXT, 0)
                        hasLoadedNext = true
                        next.sortedWith(
                            compareByDescending<Event> {it.round}
                                .thenByDescending { it.startDate }
                        )
                    } else {
                        api.getTournamentEvents(eventSource.tournamentId, Span.LAST, 0)
                            .sortedWith(
                            compareByDescending<Event> {it.round}
                                .thenByDescending { it.startDate }
                        )
                    }
                }

                is EventSource.Player -> {
                    if(page == 0 && !hasLoadedNext) {
                        val next = api.getPlayerEvents(eventSource.playerId, Span.NEXT, 0)
                        hasLoadedNext = true
                        next.sortedWith(
                            compareByDescending<Event> {it.round}
                                .thenByDescending { it.startDate }
                        )
                    } else {
                        api.getPlayerEvents(eventSource.playerId, Span.LAST, 0)
                            .sortedWith(
                                compareByDescending<Event> {it.round}
                                    .thenByDescending { it.startDate }
                            )
                    }
                }
            }
            LoadResult.Page(
                data = events,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (events.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
