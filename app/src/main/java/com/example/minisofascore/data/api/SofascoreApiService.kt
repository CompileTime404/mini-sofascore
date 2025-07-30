package com.example.minisofascore.data.api

import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Incident
import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Span
import com.example.minisofascore.data.model.Standings
import com.example.minisofascore.data.model.Tournament
import retrofit2.http.GET
import retrofit2.http.Path

interface SofascoreApiService {
    @GET("sport/{slug}/events/{date}")
    suspend fun getEvents(@Path("slug") slug: String, @Path("date") date: String): List<Event>

    @GET("tournament/{id}")
    suspend fun getTournament(@Path("id") id: Int): Tournament

    @GET("tournament/{id}/standings")
    suspend fun getTournamentStandings(@Path("id") id: Int): List<Standings>

    @GET("tournament/{id}/events/{span}/{page}")
    suspend fun getTournamentEvents(
        @Path("id") id: Int,
        @Path("span") span: Span,
        @Path("page") page: Int
    ): List<Event>

    @GET("event/{id}")
    suspend fun getEvent(@Path("id") id: Int): Event

    @GET("event/{id}/incidents")
    suspend fun getEventIncidents(@Path("id") id: Int): List<Incident>

    @GET("sport/{slug}/tournaments")
    suspend fun getTournaments(@Path("slug") slug: String): List<Tournament>

    @GET("player/{id}")
    suspend fun getPlayer(@Path("id") id: Int): Player

    @GET("player/{id}/events/{span}/{page}")
    suspend fun getPlayerEvents(
        @Path("id") id: Int,
        @Path("span") span: Span,
        @Path("page") page: Int
    ) : List<Event>

    @GET("team/{id}/tournaments")
    suspend fun getTeamTournaments(@Path("id") id: Int): List<Tournament>
}
