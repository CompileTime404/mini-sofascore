package com.example.minisofascore.ui.playedetailspage

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Country
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Sport
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.ui.shared.EventRow
import com.example.minisofascore.util.ageInYears
import com.example.minisofascore.util.formatToDisplay
import com.example.minisofascore.util.getCountryFlagUrl
import com.example.minisofascore.util.getPlayerImageUrl
import com.example.minisofascore.util.getTeamLogoUrl
import com.example.minisofascore.util.getTournamentLogoUrl
import com.example.minisofascore.util.transformPlayerPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayerDetailsScreenRoot(
    playerId: Int,
    viewModel: PlayerDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTournamentClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        PlayerDetailsUiEvent.NavigateBack -> onBackClick()
                        is PlayerDetailsUiEvent.NavigateToTournamentDetails -> onTournamentClick(
                            event.tournamentId
                        )

                        is PlayerDetailsUiEvent.NavigateToEventDetails -> onEventClick(event.eventId)
                        is PlayerDetailsUiEvent.NavigateToTeamDetails -> onTeamClick(event.teamId)
                        is PlayerDetailsUiEvent.ShowSnackbar -> Toast.makeText(
                            context,
                            event.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerEvents = viewModel.listOfEvents.collectAsLazyPagingItems()

    PlayerDetailsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        playerEvents = playerEvents,
        modifier = modifier
    )
}

@Composable
private fun PlayerDetailsScreen(
    uiState: PlayerDetailsUiState,
    onAction: (PlayerDetailsUiAction) -> Unit,
    playerEvents: LazyPagingItems<Event>,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            PlayerDetailsTopBar(
                player = uiState.player,
                onBackClick = { onAction(PlayerDetailsUiAction.ClickBackButton) }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier.padding(it)
        ) {
            item {
                PlayerDetailsSection(player = uiState.player)
            }
            item {
                MatchesSection()
            }
            item {
                PlayerLeagueSection(
                    tournament = uiState.tournament,
                    onTournamentClick = { onAction(PlayerDetailsUiAction.ClickTournament(it)) }
                )
            }
            items(playerEvents.itemSnapshotList) {
                EventRow(
                    event = it ?: return@items,
                    isOnTournamentOrPlayerScreen = true,
                    onClick = { onAction(PlayerDetailsUiAction.ClickEvent(it.id)) },
                    eventStatusEnum = it.status
                )
            }
        }
    }
}


@Composable
private fun PlayerDetailsTopBar(
    player: Player?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .background(colors.primary)
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = colors.onPrimary)
        }

        Row(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = getPlayerImageUrl(player?.id ?: -1),
                    contentDescription = "Player image",
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = player?.name ?: "Unknown player",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.W700,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PlayerDetailsSection(
    player: Player?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = getTeamLogoUrl(player?.team?.id ?: -1),
                contentDescription = "Team image",
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = player?.team?.name ?: "Unknown team",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                color = colors.onBackground
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerInfoBox(
                title = "Nationality",
                value = player?.country?.name?.take(3) ?: "Unknown",
                flag = getCountryFlagUrl(player?.country?.name ?: "")
            )
            PlayerInfoBox(
                title = "Position",
                value = transformPlayerPosition(player?.position ?: "Unknown")
            )
            PlayerInfoBox(
                title = player?.dateOfBirth?.formatToDisplay() ?: "Unknown",
                value = (player?.dateOfBirth?.ageInYears() ?: "Unknown").toString() + " Yrs"
            )
        }
    }
}

@Composable
fun PlayerInfoBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    flag: String? = null
) {

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .width(110.dp)
            .background(colors.secondary, shape = RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W700,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            color = colors.onBackground.copy(0.4f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (flag != null) {
                AsyncImage(
                    model = flag,
                    contentDescription = "Country flag",
                    modifier = Modifier
                        .size(16.dp)
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier.width(2.dp))
            }
            Text(
                text = value,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                color = colors.onBackground
            )
        }
    }
}

@Composable
private fun MatchesSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Matches",
                color = colors.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun PlayerLeagueSection(
    tournament: Tournament?,
    onTournamentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onTournamentClick(tournament?.id ?: -1) })
            .background(colors.onPrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = getTournamentLogoUrl(tournament?.id ?: -1),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(30.dp))
        Text(
            text = tournament?.country?.name ?: "Unknown",
            fontWeight = FontWeight.W700,
            color = colors.onBackground,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "›  ${tournament?.name.orEmpty()}",
            color = colors.onBackground.copy(alpha = 0.4f),
            fontWeight = FontWeight.W700,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
    }
}




@Preview(showBackground = true)
@Composable
fun PlayerDetailsTopBarPreview() {
    PlayerDetailsTopBar(
        player = Player(
            id = 1, name = "Luka Modrić", country = Country(
                id = 3,
                name = "Croatia",
            ),
            team = Team3(
                id = 1,
                name = "Real Madrid",
                country = Country(
                    id = 1,
                    name = "Spain"
                ),
            ), position = "MIDFIELDER",
            slug = "football",
            sport = Sport(
                id = 1,
                name = "Football",
                slug = "football"
            ),
            dateOfBirth = LocalDate.parse("1985-09-09", DateTimeFormatter.ISO_DATE).toString()
        ),
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerDetailsSectionPreview() {
    PlayerDetailsSection(
        player = Player(
            id = 1,
            name = "Luka Modrić",
            country = Country(
                id = 1,
                name = "Croatia"
            ),
            team = Team3(
                id = 10,
                name = "Real Madrid",
                country = Country(
                    id = 1,
                    name = "Spain"
                )
            ),
            position = "MIDFIELDER",
            dateOfBirth = LocalDate.parse("1985-09-09", DateTimeFormatter.ISO_DATE).toString(),
            slug = "football",
            sport = Sport(
                id = 1,
                name = "Football",
                slug = "football"
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerInfoBoxPreview() {
    PlayerInfoBox(
        title = "Nationality",
        value = "CRO",
        flag = getCountryFlagUrl("Croatia")
    )
}

@Preview(showBackground = true)
@Composable
fun MatchesSectionPreview() {
    MatchesSection()
}

@Preview(showBackground = true)
@Composable
fun PlayerLeagueSectionPreview() {
    PlayerLeagueSection(
        tournament = Tournament(
            id = 101,
            name = "UEFA Champions League",
            slug = "uefa-champions-league",
            country = Country(
                id = 1,
                name = "Europe",
                ),
            sport = Sport(
                id = 1,
                name = "Football",
                slug = "football"
            ),
        ),
        onTournamentClick = {}
    )
}





