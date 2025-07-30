package com.example.minisofascore.util

import com.example.minisofascore.data.model.Country
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.EventWinnerCodeEnum
import com.example.minisofascore.data.model.Score
import com.example.minisofascore.data.model.Sport
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Event.hasHomeWon(): Boolean = winnerCode == EventWinnerCodeEnum.HOME
fun Event.hasAwayWon(): Boolean = winnerCode == EventWinnerCodeEnum.AWAY
fun Event.hasNotStarted(): Boolean = status == EventStatusEnum.NOT_STARTED
fun Event.hasFinished(): Boolean = status == EventStatusEnum.FINISHED

fun <T> pollingFlow(interval: Long = 15000, fetch: suspend () -> T): Flow<T> {
    return flow {
        while (true) {
            emit(fetch())
            delay(interval)
        }
    }
}

fun getTeamLogoUrl(teamId: Int): String {
    return "https://academy-backend.sofascore.dev/team/$teamId/image"
}

fun getTournamentLogoUrl(tournamentId: Int): String {
    return "https://academy-backend.sofascore.dev/tournament/$tournamentId/image"
}

fun getPlayerImageUrl(playerId: Int): String {
    return "https://academy-backend.sofascore.dev/player/$playerId/image"
}

fun transformPlayerPosition(position: String): String {
    return when (position) {
        "G" -> "Goalkeeper"
        "D" -> "Defender"
        "M" -> "Midfielder"
        "F" -> "Forward"
        else -> ""
    }
}

fun String.formatToDisplay(): String {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val parse = OffsetDateTime.parse(this, formatter)
    return parse.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
}

fun String.ageInYears(): Int {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val dob = OffsetDateTime.parse(this, formatter).toLocalDate()
    return Period.between(dob, LocalDate.now()).years
}

fun getCountryFlagUrl(countryName: String): String {
    val isoCode = when (countryName.lowercase()) {
        "spain" -> "es"
        "croatia" -> "hr"
        "england" -> "gb"
        "france" -> "fr"
        "germany" -> "de"
        "usa" -> "us"
        "japan" -> "jp"
        else -> null
    }
    return isoCode?.let { "https://flagcdn.com/w40/$it.png" } ?: ""
}

fun String.toZonedDateTime(): ZonedDateTime = Instant.parse(this).atZone(ZoneId.systemDefault())

fun ZonedDateTime.toDateString(): String =
    toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yy."))

fun ZonedDateTime.toTimeString(): String =
    toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

fun String.formatPeriodScore(): String {
    val parts = this.split(" ")
    return if (parts.size == 2) {
        val period = parts[0]
        val score = parts[1]
        val formattedScore = score.replace(":", " - ")
        "$period ( $formattedScore )"
    } else {
        this
    }
}

fun Event.getHeaderText(): String {
    return "%s, %s, %s, Round %s".format(
        tournament.sport.name,
        tournament.country.name,
        tournament.name,
        round
    )
}

object SampleData {

    fun finishedEvent(): Event = Event(
        id = 999,
        round = 32,
        winnerCode = EventWinnerCodeEnum.HOME,
        status = EventStatusEnum.FINISHED,
        startDate = "2025-06-12T19:05:00Z",
        homeTeam = Team3(1, "Manchester United", country = Country(1, "England")),
        awayTeam = Team3(2, "Barcelona", country = Country(2, "Spain")),
        homeScore = Score(2),
        awayScore = Score(3),
        tournament = Tournament(
            id = 77,
            name = "Premier League",
            slug = "premier-league",
            sport = Sport(1, "football", "Football"),
            country = Country(1, "England")
        ),
        slug = "football"
    )

    fun inProgressEvent(): Event = Event(
        id = 999,
        round = 32,
        winnerCode = null,
        status = EventStatusEnum.IN_PROGRESS,
        startDate = "2025-06-12T19:05:00Z",
        homeTeam = Team3(1, "Manchester United", country = Country(1, "England")),
        awayTeam = Team3(2, "Barcelona", country = Country(2, "Spain")),
        homeScore = Score(2),
        awayScore = Score(3),
        tournament = Tournament(
            id = 77,
            name = "Premier League",
            slug = "premier-league",
            sport = Sport(1, "football", "Football"),
            country = Country(1, "England")
        ),
        slug = "football"
    )

    fun notStartedEvent(): Event = Event(
        id = 12345,
        round = 5,
        winnerCode = null,
        status = EventStatusEnum.NOT_STARTED,
        startDate = "2025-12-15T18:00:00Z",
        homeTeam = Team3(1, "Manchester United", Country(1, "England")),
        awayTeam = Team3(2, "Barcelona", Country(2, "Spain")),
        homeScore = Score(0),
        awayScore = Score(0),
        tournament = Tournament(
            id = 77,
            name = "Premier League",
            slug = "premier-league",
            sport = Sport(1, "football", "Football"),
            country = Country(1, "England")
        ),
        slug = "football"
    )
}

