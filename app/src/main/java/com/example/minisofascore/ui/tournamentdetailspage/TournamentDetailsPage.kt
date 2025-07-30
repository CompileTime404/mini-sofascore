package com.example.minisofascore.ui.tournamentdetailspage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Country
import com.example.minisofascore.data.model.SortedStandingsRow
import com.example.minisofascore.data.model.Sport
import com.example.minisofascore.data.model.Standings
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.ui.shared.EventRow
import com.example.minisofascore.ui.theme.MiniSofascoreTheme
import com.example.minisofascore.util.getCountryFlagUrl
import com.example.minisofascore.util.getTournamentLogoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TournamentDetailsScreenRoot(
    tournamentId: Int,
    modifier: Modifier = Modifier,
    viewModel: TournamentDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTeamClick: (Int) -> Unit,
    onEventClick: (Int) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val listOfDisplayItems = viewModel.listOfDisplayItems.collectAsLazyPagingItems()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is TournamentDetailsUiEvent.NavigateBack -> onBackClick()
                        is TournamentDetailsUiEvent.NavigateToTeamDetails -> onTeamClick(event.teamId)
                        is TournamentDetailsUiEvent.NavigateToEventDetails -> onEventClick(event.eventId)
                        is TournamentDetailsUiEvent.ShowSnackbar ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    TournamentDetailsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        listOfEvents = listOfDisplayItems,
        modifier = modifier
    )
}

@Composable
private fun TournamentDetailsScreen(
    uiState: TournamentDetailsUiState,
    onAction: (TournamentDetailsUiAction) -> Unit,
    listOfEvents: LazyPagingItems<DisplayItem>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TournamentTopBar(
                tournament = uiState.tournament,
                onBackClick = { onAction(TournamentDetailsUiAction.ClickBackButton) }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TournamentTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onAction(TournamentDetailsUiAction.SelectTab(it)) }
            )

            when (uiState.selectedTab) {
                TournamentTab.MATCHES -> TournamentMatchesTabContent(
                    pagingItems = listOfEvents,
                    onEventClick = { onAction(TournamentDetailsUiAction.ClickEvent(it)) }
                )

                TournamentTab.STANDINGS -> TournamentStandingsTabContent(
                    standings = uiState.standings,
                    onTeamClick = { onAction(TournamentDetailsUiAction.ClickTeam(it)) }
                )
            }
        }
    }
}

@Composable
private fun TournamentTopBar(
    tournament: Tournament?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tournament == null) return

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .background(colors.primary)
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.onPrimary
            )
        }

        Row {
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = getTournamentLogoUrl(tournament.id),
                    contentDescription = "Tournament logo",
                    modifier = Modifier.size(48.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = tournament.name,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
                Row {
                    AsyncImage(
                        model = getCountryFlagUrl(tournament.country.name),
                        contentDescription = "Flag",
                        modifier = Modifier
                            .size(16.dp)
                            .clip(shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tournament.country.name,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun TournamentTabRow(
    selectedTab: TournamentTab,
    onTabSelected: (TournamentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = TournamentTab.entries.toTypedArray()
    val colors = MaterialTheme.colorScheme

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = modifier
            .fillMaxWidth()
            .background(color = colors.primary),
        containerColor = colors.primary,
        contentColor = colors.onPrimary,
        divider = {},
        indicator = { tabPositions ->
            val index = tabs.indexOf(selectedTab)
            val tabPos = tabPositions[index]
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPos)
                    .height(4.dp)
                    .padding(horizontal = 8.dp)
                    .background(colors.onPrimary, RoundedCornerShape(1.5.dp))
            )
        }
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                        color = colors.onPrimary
                    )
                }
            )
        }
    }
}

@Composable
private fun TournamentMatchesTabContent(
    pagingItems: LazyPagingItems<DisplayItem>,
    onEventClick: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pagingItems.itemCount) { index ->
            when (val item = pagingItems[index]) {
                is DisplayItem.RoundHeaderItem -> {
                    Text(
                        text = "Round ${item.round}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = colors.onPrimaryContainer)
                            .padding(8.dp)
                            .padding(top = 12.dp, start = 12.dp),
                        fontWeight = FontWeight.W700,
                        color = colors.onBackground,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp
                    )
                }

                is DisplayItem.EventItem -> {
                    EventRow(
                        event = item.event,
                        isOnTournamentOrPlayerScreen = true,
                        onClick = { onEventClick(item.event.id) },
                        eventStatusEnum = item.event.status,
                    )
                }
                else -> Unit
            }
        }

        pagingItems.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    item {
                        Text(
                            text = "Failed to load more events.",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val error = (loadState.refresh as LoadState.Error).error
                    item {
                        Text(
                            text = "Error: ${error.localizedMessage ?: "Unknown error"}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun TournamentStandingsTabContent(
    standings: Standings?,
    onTeamClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StandingsHeaderRow()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            standings?.sortedStandingsRows?.forEach { row ->
                item {
                    StandingsRow(
                        row = row,
                        index = standings.sortedStandingsRows.indexOf(row),
                        onTeamClick = onTeamClick
                    )
                }
            } ?: item {
                Text(
                    text = "No standings available.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
private fun StandingsHeaderRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "#",
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
        Text(
            " Team",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
        Text(
            "P",
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "W",
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "D",
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "L",
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "Goals",
            modifier = Modifier.width(48.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "PTS",
            modifier = Modifier.width(32.dp),
            fontWeight = FontWeight.W400,
            color = colors.onBackground.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun StandingsRow(
    row: SortedStandingsRow,
    index: Int,
    onTeamClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val goals = "${row.scoresFor}:${row.scoresAgainst}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTeamClick(row.team.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-8).dp)
                .size(24.dp)
                .background(color = Color(0xFFF0EEDF), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.surfaceContainer,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
            )
        }
        Text(
            text = row.team.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground
        )
        Text(
            text = row.played.toString(), modifier = Modifier.width(24.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = row.wins.toString(),
            modifier = Modifier.width(24.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = row.draws.toString(),
            modifier = Modifier.width(24.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = row.losses.toString(),
            modifier = Modifier.width(24.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = goals,
            modifier = Modifier.width(48.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = row.points.toString(),
            modifier = Modifier.width(32.dp),
            color = colors.onBackground,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TournamentTopBarPreview() {
    MiniSofascoreTheme {
        TournamentTopBar(
            tournament = Tournament(
                id = 1,
                name = "Premier League",
                slug = "premier-league",
                country = Country(1, "England"),
                sport = Sport(1, "football", "Football")
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TournamentTabRowPreview() {
    MiniSofascoreTheme {
        TournamentTabRow(
            selectedTab = TournamentTab.MATCHES,
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StandingsHeaderRowPreview() {
    MiniSofascoreTheme {
        StandingsHeaderRow()
    }
}

@Preview(showBackground = true)
@Composable
private fun StandingsRowPreview() {
    MiniSofascoreTheme {
        StandingsRow(
            row = SortedStandingsRow(
                team = Team3(
                    1,
                    "Manchester United",
                    Country(1, "England")
                ),
                played = 20,
                wins = 15,
                draws = 3,
                losses = 2,
                scoresFor = 45,
                scoresAgainst = 20,
                points = 48,
                id = 1,
                percentage = 0.46f
            ),
            index = 0,
            onTeamClick = {}
        )
    }
}
