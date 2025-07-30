package com.example.minisofascore.ui.shared

import android.R.attr.name
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.util.SampleData
import com.example.minisofascore.util.getTeamLogoUrl
import com.example.minisofascore.util.hasAwayWon
import com.example.minisofascore.util.hasFinished
import com.example.minisofascore.util.hasHomeWon
import com.example.minisofascore.util.hasNotStarted
import com.example.minisofascore.util.toDateString
import com.example.minisofascore.util.toTimeString
import com.example.minisofascore.util.toZonedDateTime

@SuppressLint("DefaultLocale")
@Composable
fun EventRow(
    event: Event,
    isOnTournamentOrPlayerScreen: Boolean,
    onClick: () -> Unit,
    eventStatusEnum: EventStatusEnum,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val isFinished = event.hasFinished()
    val isLive = event.status == EventStatusEnum.IN_PROGRESS
    val isNotStarted = event.hasNotStarted()

    val homeLost = isFinished && !event.hasHomeWon()
    val awayLost = isFinished && !event.hasAwayWon()

    val homeColor = when {
        event.hasHomeWon() -> colors.onBackground
        !event.hasHomeWon() && !event.hasAwayWon() -> colors.onBackground.copy(alpha = 0.4f)
        else -> colors.onBackground.copy(alpha = 0.4f)
    }

    val awayColor = when {
        event.hasAwayWon() -> colors.onBackground
        !event.hasHomeWon() && !event.hasAwayWon() -> colors.onBackground.copy(alpha = 0.4f)
        else -> colors.onBackground.copy(alpha = 0.4f)
    }


    val startTime = remember(event.startDate) {
        try {
            event.startDate?.toZonedDateTime()?.toTimeString() ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

       val statusText = when {
           isLive -> "36'"
           isNotStarted -> "-"
           else -> "FT"

       }

        val statusColor = when{
            isLive -> colors.outlineVariant
            else -> colors.onBackground.copy(alpha = 0.4f)
        }

        EventStatusColumn(
            isOnTournamentScreen = isOnTournamentOrPlayerScreen,
            event = event,
            startTime = startTime,
            bottomText = statusText,
            bottomTextColor = statusColor
        )

        Spacer(modifier = Modifier.width(12.dp))

        VerticalDivider(
            thickness = 1.dp,
            color = colors.onBackground.copy(alpha = 0.1f),
            modifier = Modifier.height(40.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            TeamRow(
                logoUrl = getTeamLogoUrl(event.homeTeam.id),
                name = event.homeTeam.name,
                color = homeColor,
                score = event.homeScore.total.toString()
            )

            Spacer(modifier = Modifier.height(4.dp))

            TeamRow(
                logoUrl = getTeamLogoUrl(event.awayTeam.id),
                name = event.awayTeam.name,
                color = awayColor,
                score = event.awayScore.total.toString()
            )
        }
    }
}

@Composable
private fun EventStatusColumn(
    isOnTournamentScreen: Boolean,
    event: Event,
    startTime: String,
    bottomText: String,
    bottomTextColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        if(isOnTournamentScreen){
            Text(
                event.startDate?.toZonedDateTime()?.toDateString() ?: "",
                color = colors.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
        } else{
            Text(
                startTime,
                color = colors.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            bottomText,
            color = bottomTextColor,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun TeamRow(
    logoUrl: String,
    name: String,
    color: Color,
    score: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = "$name logo",
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = name,
            color = color,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.W400
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = score,
            color = color,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.W400
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamRowPreview() {
    TeamRow(
        logoUrl = "https://academy-backend.sofascore.dev/team/1/image",
        name = "Arsenal",
        color = Color.Black,
        score = "2"
    )
}

@Preview(showBackground = true)
@Composable
private fun EventStatusColumnPreview() {
    EventStatusColumn(
        isOnTournamentScreen = false,
        event = SampleData.notStartedEvent(),
        startTime = "18:30",
        bottomText = "FT",
        bottomTextColor = Color.Red
    )
}

@Preview(showBackground = true)
@Composable
private fun EventRowPreview() {
    EventRow(
        event = SampleData.finishedEvent(),
        isOnTournamentOrPlayerScreen = false,
        onClick = {},
        eventStatusEnum = EventStatusEnum.FINISHED
    )
}



