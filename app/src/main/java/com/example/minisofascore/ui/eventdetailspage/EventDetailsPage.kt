package com.example.minisofascore.ui.eventdetailspage

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.minisofascore.R
import com.example.minisofascore.data.model.CardColorEnum
import com.example.minisofascore.data.model.CardIncident
import com.example.minisofascore.data.model.CardTeamSideEnum
import com.example.minisofascore.data.model.CompactPlayer
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.GoalIncident
import com.example.minisofascore.data.model.GoalScoringTeamEnum
import com.example.minisofascore.data.model.GoalTypeEnum
import com.example.minisofascore.data.model.Incident
import com.example.minisofascore.data.model.PeriodIncident
import com.example.minisofascore.ui.theme.MiniSofascoreTheme
import com.example.minisofascore.util.SampleData
import com.example.minisofascore.util.formatPeriodScore
import com.example.minisofascore.util.getHeaderText
import com.example.minisofascore.util.getTeamLogoUrl
import com.example.minisofascore.util.getTournamentLogoUrl
import com.example.minisofascore.util.hasAwayWon
import com.example.minisofascore.util.hasHomeWon
import com.example.minisofascore.util.toDateString
import com.example.minisofascore.util.toTimeString
import com.example.minisofascore.util.toZonedDateTime
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

@Composable
fun EventDetailsScreenRoot(
    eventId: Int,
    modifier: Modifier = Modifier,
    viewModel: EventDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTeamClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onTournamentClick: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    val view = LocalView.current
    val window = (view.context as Activity).window

    DisposableEffect(Unit) {
        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = true

        onDispose {
            controller.isAppearanceLightStatusBars = false
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is EventDetailsUiEvent.NavigateBack -> onBackClick()
                        is EventDetailsUiEvent.NavigateToTeamDetails -> onTeamClick(event.teamId)
                        is EventDetailsUiEvent.NavigateToPlayerDetails -> onPlayerClick(event.playerId)
                        is EventDetailsUiEvent.NavigateToTournamentDetails -> onTournamentClick(event.tournamentId)
                        is EventDetailsUiEvent.ShowSnackbar ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    EventDetailsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDetailsScreen(
    uiState: EventDetailsUiState,
    onAction: (EventDetailsUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val headerText = uiState.event?.getHeaderText().orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = getTournamentLogoUrl(uiState.event?.tournament?.id ?: -1),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = headerText,
                            style = typography.titleSmall,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 0.sp,
                            color = colors.onBackground.copy(alpha = 0.4f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(EventDetailsUiAction.ClickBackButton) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

            )
        },
        modifier = modifier
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.onPrimaryContainer)
                    .padding(padding)
            ) {

                stickyHeader {
                    uiState.event?.let { event ->
                        EventHeader(
                            event = event,
                            status = uiState.eventStatus,
                            onAction = onAction
                        )
                    }
                }

                item {
                    Surface(modifier = Modifier.height(8.dp)) {}
                }

                when (uiState.eventStatus) {

                    EventStatusEnum.FINISHED -> items(uiState.eventIncidents.reversed()) { incident ->
                        IncidentRow(
                            incident = incident,
                            eventStatus = uiState.eventStatus,
                            onPlayerClick = { id -> onAction(EventDetailsUiAction.ClickPlayer(id)) }
                        )
                    }

                    EventStatusEnum.NOT_STARTED -> item {
                        val tournamentId = uiState.event?.tournament?.id
                        if (tournamentId != null) {
                            NotStartedPlaceholder(
                                tournamentId = tournamentId,
                                onTournamentClick = {
                                    onAction(EventDetailsUiAction.ClickTournament(tournamentId))
                                }
                            )
                        }
                    }

                    EventStatusEnum.IN_PROGRESS -> items(uiState.eventIncidents.reversed()) { incident ->
                        IncidentRow(
                            incident = incident,
                            eventStatus = uiState.eventStatus,
                            onPlayerClick = { id -> onAction(EventDetailsUiAction.ClickPlayer(id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamBlock(
    name: String,
    logoUrl: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = "$name logo",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            style = typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.W700,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun EventHeader(
    event: Event,
    status: EventStatusEnum,
    onAction: (EventDetailsUiAction) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val homeColor = when {
        status == EventStatusEnum.FINISHED && event.hasHomeWon() -> colors.onBackground
        status == EventStatusEnum.FINISHED && event.hasAwayWon() -> colors.onBackground.copy(alpha = 0.4f)
        else -> colors.onBackground
    }

    val awayColor = when {
        status == EventStatusEnum.FINISHED && event.hasAwayWon() -> colors.onBackground
        status == EventStatusEnum.FINISHED && event.hasHomeWon() -> colors.onBackground.copy(alpha = 0.4f)
        else -> colors.onBackground
    }

    val zoned = remember(event.startDate) { event.startDate?.toZonedDateTime() ?: ZonedDateTime.now() }
    val dateStr = remember(zoned) { zoned.toDateString() }
    val timeStr = remember(zoned) { zoned.toTimeString() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamBlock(
                name = event.homeTeam.name,
                logoUrl = getTeamLogoUrl(event.homeTeam.id),
                onClick = { onAction(EventDetailsUiAction.ClickTeam(event.homeTeam.id)) }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (status) {
                    EventStatusEnum.FINISHED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = event.homeScore.total.toString(),
                                style = typography.bodyMedium,
                                fontWeight = FontWeight.W700,
                                fontSize = 32.sp,
                                color = homeColor
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("-", color = colors.onBackground.copy(alpha = 0.4f))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = event.awayScore.total.toString(),
                                style = typography.bodyMedium,
                                fontWeight = FontWeight.W700,
                                fontSize = 32.sp,
                                color = awayColor
                            )
                        }
                        Text("Full Time", style = typography.labelMedium, color = colors.onSurface.copy(alpha = 0.4f))
                    }

                    EventStatusEnum.IN_PROGRESS -> {
                        Text(
                            "${event.homeScore.total} - ${event.awayScore.total}",
                            style = typography.headlineMedium,
                            fontWeight = FontWeight.W700,
                            fontSize = 32.sp,
                            color = colors.outlineVariant
                        )
                        Text("36'", style = typography.labelMedium, color = colors.outlineVariant)
                    }

                    EventStatusEnum.NOT_STARTED -> {
                        Text(dateStr, color = colors.onBackground, fontSize = 12.sp)
                        Text(timeStr, color = colors.onBackground, fontSize = 12.sp)
                    }
                }
            }

            TeamBlock(
                name = event.awayTeam.name,
                logoUrl = getTeamLogoUrl(event.awayTeam.id),
                onClick = { onAction(EventDetailsUiAction.ClickTeam(event.awayTeam.id)) }
            )
        }
    }

    HorizontalDivider(thickness = 1.dp, color = Color.White)
}


@Composable
private fun IncidentRow(
    incident: Incident,
    eventStatus: EventStatusEnum,
    modifier: Modifier = Modifier,
    onPlayerClick: (Int) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    when (incident) {

        is PeriodIncident -> {
            val textColor = when (eventStatus) {
                EventStatusEnum.IN_PROGRESS -> colors.outlineVariant
                else -> MaterialTheme.colorScheme.onBackground
            }
            Surface(
                color = colors.secondary,
                shape = RoundedCornerShape(16),
                modifier = modifier
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = incident.text.formatPeriodScore(),
                    color = textColor,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W700,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp
                )
            }
        }

        is GoalIncident -> {
            val isHome = incident.scoringTeam == GoalScoringTeamEnum.HOME
            val icon = painterResource(R.drawable.colored_shape)

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(colors.onSecondary)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isHome) {
                    IncidentContentBlock(
                        incident = incident,
                        playerName = incident.player.name,
                        result = "${incident.homeScore} - ${incident.awayScore}",
                        icon = icon,
                        iconTint = colors.surfaceTint,
                        isHome = true,
                        textAlign = TextAlign.Start,
                        onClick = { onPlayerClick(incident.player.id) }
                    )
                } else {
                    IncidentContentBlock(
                        incident = incident,
                        playerName = incident.player.name,
                        result = "${incident.homeScore} - ${incident.awayScore}",
                        icon = icon,
                        iconTint = colors.surfaceTint,
                        isHome = false,
                        textAlign = TextAlign.End,
                        onClick = { onPlayerClick(incident.player.id) }
                    )
                }
            }
        }

        is CardIncident -> {
            val isHome = incident.teamSide == CardTeamSideEnum.HOME
            val tint = when (incident.color) {
                CardColorEnum.YELLOW -> Color(0xFFFFC400)
                CardColorEnum.YELLOWRED -> Color(0xFFFF6D00)
                CardColorEnum.RED -> Color(0xFFD32F2F)
            }
            val icon = painterResource(R.drawable.red_card)

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(colors.onSecondary)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IncidentContentBlock(
                    incident = incident,
                    icon = icon,
                    playerName = incident.player.name,
                    result = "",
                    iconTint = tint,
                    isHome = isHome,
                    textAlign = if (isHome) TextAlign.Start else TextAlign.End,
                    onClick = { onPlayerClick(incident.player.id) }
                )
            }
        }

    }
}

@Composable
private fun IncidentContentBlock(
    incident: Incident,
    playerName: String,
    result: String,
    icon: Painter,
    iconTint: Color,
    isHome: Boolean,
    textAlign: TextAlign,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isHome) Arrangement.Start else Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isHome) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(bottom = 2.dp)
                )
                Text(
                    text = "${incident.time}'",
                    color = colors.onBackground.copy(0.4f),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp
                )
            }
            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
                    .width(1.dp),
                color = colors.onBackground.copy(0.1f)
            )
            if (result.isNotBlank()) {
                Text(
                    text = result,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.W700,
                    textAlign = textAlign,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = playerName,
                style = typography.bodyMedium,
                fontWeight = FontWeight.W400,
                textAlign = textAlign,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                color = colors.onBackground,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onClick)
            )
        } else {

            Text(
                text = playerName,
                style = typography.bodyMedium,
                fontWeight = FontWeight.W400,
                textAlign = textAlign,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                color = colors.onBackground,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(onClick = onClick)
            )

            if (result.isNotBlank()) {
                Text(
                    text = result,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.W700,
                    textAlign = textAlign,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(start = 8.dp)
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
                    .width(1.dp),
                color = colors.onBackground.copy(0.1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(bottom = 2.dp)
                )
                Text(
                    text = "${incident.time}'",
                    color = colors.onBackground.copy(0.4f),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}

@Composable
private fun NotStartedPlaceholder(
    tournamentId: Int,
    onTournamentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = colors.outline.copy(0.2f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "No results yet.",
                style = typography.bodyMedium,
                color = colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onTournamentClick(tournamentId) },
            border = BorderStroke(2.dp, colors.primary),
            shape = RoundedCornerShape(2.dp),
        ) {
            Text(
                text = "View Tournament Details",
                color = colors.primary,
                style = typography.labelLarge,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailsScreenPreview() {
    val mock = SampleData.inProgressEvent()
    MiniSofascoreTheme {
        EventDetailsScreen(
            uiState = EventDetailsUiState(
                isLoading = false,
                event = mock,
                eventStatus = mock.status,
                eventIncidents = persistentListOf()
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamBlockPreview() {
    MiniSofascoreTheme {
        TeamBlock(
            name = "Barcelona",
            logoUrl = getTeamLogoUrl(2),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventHeaderPreviewFinished() {
    EventHeader(
        event = SampleData.finishedEvent(),
        status = EventStatusEnum.FINISHED,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EventHeaderPreviewInProgress() {
    EventHeader(
        event = SampleData.inProgressEvent(),
        status = EventStatusEnum.IN_PROGRESS,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EventHeaderPreviewNotStarted() {
    EventHeader(
        event = SampleData.notStartedEvent(),
        status = EventStatusEnum.NOT_STARTED,
        onAction = {}
    )
}


@Preview(showBackground = true)
@Composable
private fun NotStartedPlaceholderPreview() {
    MiniSofascoreTheme {
        NotStartedPlaceholder(tournamentId = 77, onTournamentClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentRowPreview() {
    val incident = GoalIncident(
        id = 1,
        player = CompactPlayer(
            id = 10,
            name = "Cristiano Ronaldo"
        ),
        time = 45,
        scoringTeam = GoalScoringTeamEnum.HOME,
        homeScore = 1,
        awayScore = 0,
        goalType = GoalTypeEnum.REGULAR
    )

    MiniSofascoreTheme {
        IncidentRow(
            incident = incident,
            eventStatus = EventStatusEnum.FINISHED,
            onPlayerClick = {}
        )
    }
}
