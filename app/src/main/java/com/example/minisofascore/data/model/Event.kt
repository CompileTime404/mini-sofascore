package com.example.minisofascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Span(val value: String){
    LAST("last"),
    NEXT("next");

    override fun toString(): String = value
}

@Serializable
enum class EventStatusEnum{
    @SerialName("notstarted") NOT_STARTED,
    @SerialName("inprogress") IN_PROGRESS,
    @SerialName("finished") FINISHED
}

@Serializable
enum class EventWinnerCodeEnum{
    @SerialName("home") HOME,
    @SerialName("away") AWAY,
    @SerialName("draw") DRAW
}

@Serializable
data class Event(
    val id: Int,
    val slug: String,
    val tournament: Tournament,
    val homeTeam: Team3,
    val awayTeam: Team3,
    val status: EventStatusEnum,
    val startDate: String? = null,
    val homeScore: Score,
    val awayScore: Score,
    val winnerCode: EventWinnerCodeEnum? = null,
    val round: Int
)