package com.example.minisofascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Incident {
    abstract val id: Int
    abstract val time: Int
}

@Serializable
enum class CardTeamSideEnum{
    @SerialName("home") HOME,
    @SerialName("away") AWAY
}

@Serializable
enum class CardColorEnum{
    @SerialName("yellow") YELLOW,
    @SerialName("yellowred") YELLOWRED,
    @SerialName("red") RED
}

@Serializable
@SerialName("card")
data class CardIncident(
    val player: CompactPlayer,
    val teamSide: CardTeamSideEnum,
    val color: CardColorEnum,
    override val id: Int,
    override val time: Int
) : Incident()

@Serializable
enum class GoalScoringTeamEnum{
    @SerialName("home") HOME,
    @SerialName("away") AWAY
}

@Serializable
enum class GoalTypeEnum{
    @SerialName("extrapoint") EXTRAPOINT,
    @SerialName("fieldgoal") FIELDGOAL,
    @SerialName("safety") SAFETY,
    @SerialName("touchdown") TOUCHDOWN,
    @SerialName("threepoint") THREEPOINT,
    @SerialName("twopoint") TWOPOINT,
    @SerialName("onepoint") ONEPOINT,
    @SerialName("penalty") PENALTY,
    @SerialName("owngoal") OWNGOAL,
    @SerialName("regular") REGULAR
}

@Serializable
@SerialName("goal")
data class GoalIncident(
    val player: CompactPlayer,
    val scoringTeam: GoalScoringTeamEnum,
    val homeScore: Int,
    val awayScore: Int,
    val goalType: GoalTypeEnum,
    override val id: Int,
    override val time: Int
) : Incident()

@Serializable
@SerialName("period")
data class PeriodIncident(
    val text: String,
    override val id: Int,
    override val time: Int
) : Incident()

