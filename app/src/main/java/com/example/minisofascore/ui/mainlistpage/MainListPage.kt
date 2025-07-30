package com.example.minisofascore.ui.mainlistpage

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.minisofascore.R
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.data.model.EventStatusEnum
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.ui.shared.EventRow
import com.example.minisofascore.ui.shared.SportTabRow
import com.example.minisofascore.ui.theme.MiniSofascoreTheme
import com.example.minisofascore.util.SampleData
import com.example.minisofascore.util.getTournamentLogoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.forEachIndexed

@Composable
fun MainListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: MainListViewModel = hiltViewModel(),
    onEventClick: (Int) -> Unit,
    onTournamentClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onLeaguesClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedSportSlugState by viewModel.selectedSportSlug.collectAsStateWithLifecycle()
    val selectedDateState by viewModel.selectedDate.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is MainListUiEvent.NavigateToEventDetails -> onEventClick(event.eventId)
                        is MainListUiEvent.NavigateToTournamentDetails -> onTournamentClick(event.tournamentId)
                        MainListUiEvent.NavigateToLeagues -> onLeaguesClick()
                        MainListUiEvent.NavigateToSettings -> onSettingsClick()
                        is MainListUiEvent.ShowSnackbar -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    MainListScreen(
        uiState = uiState,
        selectedSportSlugState = selectedSportSlugState,
        selectedDateState = selectedDateState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainListScreen(
    uiState: MainListUiState,
    selectedSportSlugState: String,
    selectedDateState: LocalDate,
    onAction: (MainListUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.sofascore_lockup),
                        contentDescription = "Sofascore logo",
                        modifier = Modifier.size(width = 132.dp, height = 32.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = { onAction(MainListUiAction.ClickLeagues) }) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leagues",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = { onAction(MainListUiAction.ClickSettings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sticky filter header
            stickyHeader {
                SportTabRow(
                    selectedSportSlug = selectedSportSlugState,
                    onSportSelected = { onAction(MainListUiAction.SelectSport(it)) }
                )
                DatePickerRow(
                    dates = uiState.availableDates,
                    selectedDate = selectedDateState,
                    today = uiState.today,
                    onDateSelected = { onAction(MainListUiAction.SelectDate(it)) }
                )
            }

            // Date Header
            item {
                DateHeader(
                    selectedDate = selectedDateState,
                    today = uiState.today,
                    eventCount = uiState.eventCount
                )
            }

            // Content
            when {
                uiState.isLoading -> item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: ${uiState.errorMessage}")
                    }
                }

                uiState.isEmpty -> item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matches today")
                    }
                }

                else -> {
                    items(uiState.tournaments) { section ->
                        EventListSection(
                            tournament = section.tournament,
                            events = section.events,
                            onTournamentClick = { onAction(MainListUiAction.ClickTournament(it)) },
                            onEventClick = { onAction(MainListUiAction.ClickEvent(it)) }
                        )
                    }
                }
            }
        }
    }
}





@Composable
private fun DatePickerRow(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dates.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    val dateFmt = DateTimeFormatter.ofPattern("d.MM.")

    val listState = rememberLazyListState()
    val selectedIndex = dates.indexOf(selectedDate).coerceAtLeast(0)

    var hasScrolledInitially by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .filter { it > 0 }
            .first()

        if (!hasScrolledInitially) {
            val centerOffset = 3
            val centeredIndex = (selectedIndex - centerOffset).coerceAtLeast(0)
            listState.scrollToItem(centeredIndex)
            hasScrolledInitially = true
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            val dayLabel = if (date == today) "TODAY" else date.dayOfWeek.name.take(3)
            val dateLabel = date.format(dateFmt)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 4.dp, start = 6.dp, end = 6.dp)
                    .clickable { onDateSelected(date) }
            ) {
                Text(
                    text = dayLabel,
                    color = colors.surface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 16.sp
                )
                Text(
                    text = dateLabel,
                    color = colors.surface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 16.sp
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(4.dp)
                        .width(40.dp)
                        .background(
                            if (isSelected) colors.background else Color.Transparent,
                            RoundedCornerShape(1.5.dp)
                        )
                )
            }
        }
    }
}



@Composable
private fun DateHeader(
    selectedDate: LocalDate,
    today: LocalDate,
    eventCount: Int?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isToday = selectedDate == today
    val title = if (isToday) {
        "Today"
    } else {
        selectedDate.format(DateTimeFormatter.ofPattern("E, dd.MM.yyyy."))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = title,
                color = colors.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "$eventCount Events",
                color = colors.onBackground.copy(alpha = 0.4f),
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
private fun EventListSection(
    tournament: Tournament,
    events: List<Event>,
    onTournamentClick: (Int) -> Unit,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onTournamentClick(tournament.id) })
                .background(colors.onPrimary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = getTournamentLogoUrl(tournament.id),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(30.dp))
            Text(
                text = tournament.country.name,
                fontWeight = FontWeight.W700,
                color = colors.onBackground,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "›  ${tournament.name}",
                color = colors.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
        }
        events.forEachIndexed { index, event ->
            EventRow(
                event = event,
                isOnTournamentOrPlayerScreen = false,
                eventStatusEnum = event.status,
                onClick = { onEventClick(event.id) }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = colors.onBackground.copy(alpha = 0.1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun SportTabRowPreview() {
    MiniSofascoreTheme {
        SportTabRow(
            selectedSportSlug = "football",
            onSportSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerRowPreview() {
    val dates = listOf(
        LocalDate.now().minusDays(1),
        LocalDate.now(),
        LocalDate.now().plusDays(1)
    )

    MiniSofascoreTheme {
        DatePickerRow(
            dates = dates,
            selectedDate = LocalDate.now(),
            today = LocalDate.now(),
            onDateSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderPreview() {
    MiniSofascoreTheme {
        DateHeader(
            selectedDate = LocalDate.now(),
            today = LocalDate.now(),
            eventCount = 8
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventListSectionPreview() {
    val tournament = Tournament(
        id = 1,
        name = "Premier League",
        slug = "premier-league",
        sport = com.example.minisofascore.data.model.Sport(1, "football", "Football"),
        country = com.example.minisofascore.data.model.Country(1, "England")
    )

    val events = listOf(
        SampleData.inProgressEvent(),
        SampleData.notStartedEvent()
    )

    MiniSofascoreTheme {
        EventListSection(
            tournament = tournament,
            events = events,
            onTournamentClick = {},
            onEventClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventRowPreview() {
    MiniSofascoreTheme {
        EventRow(
            isOnTournamentOrPlayerScreen = false,
            event = SampleData.inProgressEvent(),
            eventStatusEnum = EventStatusEnum.IN_PROGRESS,
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TopAppBarPreview() {
    MiniSofascoreTheme {
        TopAppBar(
            modifier = Modifier
                .height(40.dp)
                .wrapContentHeight(),
            title = {
                Image(
                    painter = painterResource(id = R.drawable.sofascore_lockup),
                    contentDescription = "Sofascore logo",
                    modifier = Modifier.size(width = 132.dp, height = 32.dp),
                    contentScale = ContentScale.Fit
                )
            },
            actions = {
                IconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { /* No-op for preview */ }) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Leagues",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                IconButton(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = { /* No-op for preview */ }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            ),
        )
    }
}