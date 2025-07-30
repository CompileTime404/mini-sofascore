package com.example.minisofascore.data.paging

sealed class EventSource {
    data class Tournament(val tournamentId: Int) : EventSource()
    data class Player(val playerId: Int) : EventSource()
}